package de.jeha.j7.resources;

import com.codahale.metrics.annotation.Timed;
import de.jeha.j7.common.http.Headers;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;

import static de.jeha.j7.common.http.Headers.CONTENT_LENGTH;

/**
 * @author jenshadlich@googlemail.com
 */
@Path("/")
public class ProxyResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyResource.class);

    private static final String ALL_SUB_RESOURCES = "{subResources:.*}";

    private final CloseableHttpClient httpClient = buildHttpClient();

    @GET
    @Path(ALL_SUB_RESOURCES)
    @Timed
    public Response proxyGet(@Context HttpServletRequest request,
                             @Context HttpServletResponse response) {
        LOG.info("proxy GET request '{}', '{}'", request.getRequestURI(), request.getQueryString());

        final String url = buildBackendUrl(request);
        final HttpGet delegate = new HttpGet(url);

        try {
            return process(request, delegate, response);
        } catch (IOException e) {
            return serverError(e);
        }
    }

    private Response serverError(Exception exception) {
        LOG.error("Server error", exception);
        return Response.serverError().build();
    }

    @HEAD
    @Path(ALL_SUB_RESOURCES)
    @Timed
    public Response proxyHead(@Context HttpServletRequest request,
                              @Context HttpServletResponse response) {
        LOG.info("proxy HEAD request '{}', '{}'", request.getRequestURI(), request.getQueryString());

        final String url = buildBackendUrl(request);
        final HttpHead delegate = new HttpHead(url);

        try {
            return process(request, delegate, response);
        } catch (IOException e) {
            return serverError(e);
        }
    }

    @POST
    @Path(ALL_SUB_RESOURCES)
    @Timed
    public Response proxyPost(@Context HttpServletRequest request,
                              @Context HttpServletResponse response) {
        LOG.info("proxy POST request '{}', '{}'", request.getRequestURI(), request.getQueryString());

        final String url = buildBackendUrl(request);
        final HttpPost delegate = new HttpPost(url);

        try {
            final byte[] data = IOUtils.toByteArray(request.getInputStream());
            delegate.setEntity(new ByteArrayEntity(data));

            return process(request, delegate, response);
        } catch (IOException e) {
            return serverError(e);
        }
    }

    @PUT
    @Path(ALL_SUB_RESOURCES)
    @Timed
    public Response proxyPut(@Context HttpServletRequest request,
                             @Context HttpServletResponse response) {
        LOG.info("proxy PUT request '{}', '{}'", request.getRequestURI(), request.getQueryString());

        final String url = buildBackendUrl(request);
        final HttpPut delegate = new HttpPut(url);

        try {
            final byte[] data = IOUtils.toByteArray(request.getInputStream());
            delegate.setEntity(new ByteArrayEntity(data));

            return process(request, delegate, response);
        } catch (IOException e) {
            return serverError(e);
        }
    }

    @DELETE
    @Path(ALL_SUB_RESOURCES)
    @Timed
    public Response proxyDelete(@Context HttpServletRequest request,
                                @Context HttpServletResponse response) {
        LOG.info("proxy DELETE request '{}', '{}'", request.getRequestURI(), request.getQueryString());

        final String url = buildBackendUrl(request);
        final HttpDelete delegate = new HttpDelete(url);

        try {
            return process(request, delegate, response);
        } catch (IOException e) {
            return serverError(e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    private String buildBackendUrl(HttpServletRequest request) {
        String url = "http://" + chooseBackendInstance() + request.getRequestURI();
        if (!StringUtils.isEmpty(request.getQueryString())) {
            url += request.getQueryString();
        }
        return url;
    }

    private Response process(HttpServletRequest request, HttpRequestBase delegate, HttpServletResponse response)
            throws IOException {
        final CloseableHttpResponse backendResponse;
        try {
            copyHeaders(request, delegate);
            backendResponse = httpClient.execute(delegate);
        } catch (IOException e) {
            LOG.warn("502 Bad Gateway '{}'", e.getMessage(), e);
            return badGateway();
        }

        try {
            if (backendResponse.getEntity() != null && backendResponse.getEntity().isStreaming()) {
                IOUtils.copy(backendResponse.getEntity().getContent(), response.getOutputStream());
                response.flushBuffer();
            }
        } finally {
            IOUtils.closeQuietly(backendResponse);
        }

        return buildProxyResponse(backendResponse);
    }

    private Response buildProxyResponse(CloseableHttpResponse backendResponse) {
        Response proxyResponse = Response
                .status(backendResponse.getStatusLine().getStatusCode())
                .build();

        copyHeaders(backendResponse, proxyResponse);

        return proxyResponse;
    }

    private void copyHeaders(CloseableHttpResponse source, Response target) {
        for (Header header : source.getAllHeaders()) {
            target.getHeaders().add(header.getName(), header.getValue());
        }
    }

    private void copyHeaders(HttpServletRequest source, HttpRequestBase target) {
        for (String headerName : Collections.list(source.getHeaderNames())) {
            if (CONTENT_LENGTH.equals(headerName)) {
                continue;
            }
            target.setHeader(headerName, source.getHeader(headerName));
        }
    }

    private Response badGateway() throws IOException {
        return Response
                .status(Response.Status.BAD_GATEWAY)
                .entity("502 Bad Gateway")
                .build();
    }

    private void copyHeaders(CloseableHttpResponse source, HttpServletResponse target) {
        for (Header header : source.getAllHeaders()) {
            if (Headers.CONNECTION.equals(header.getName())) {
                continue;
            }
            target.setHeader(header.getName(), header.getValue());
        }
    }

    private String chooseBackendInstance() {
        return "localhost:8888";
    }

    private CloseableHttpClient buildHttpClient() {
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(30_000)
                .setSocketTimeout(30_000)
                .build();

        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(1024);

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config)
                .build();
    }
}
