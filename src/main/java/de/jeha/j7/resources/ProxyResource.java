package de.jeha.j7.resources;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * @author jenshadlich@googlemail.com
 */
@Path("/")
public class ProxyResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyResource.class);

    private final CloseableHttpClient httpClient = buildHttpClient();

    @GET
    @Path("{subResources:.*}")
    @Timed
    public void proxyGet(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        LOG.info("proxy GET request '{}', '{}'", request.getRequestURI(), request.getQueryString());

        String url = "http://" + chooseBackendInstance() + request.getRequestURI();
        if (!StringUtils.isEmpty(request.getQueryString())) {
            url += request.getQueryString();
        }

        HttpGet delegate = new HttpGet(url);

        final CloseableHttpResponse backendResponse;
        try {
            backendResponse = httpClient.execute(delegate);
        } catch (IOException e) {
            LOG.warn("502 Bad Gateway '{}'", e.getMessage());
            response.setContentType("text/plain");
            response.getOutputStream().print("502 Bad Gateway");
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.flushBuffer();
            return;
        }

        try {
            // copy headers to response
            for (Header header : backendResponse.getAllHeaders()) {
                if ("Connection".equals(header.getName())) {
                    continue;
                }
                response.setHeader(header.getName(), header.getValue());
            }
            if (backendResponse.getEntity().isStreaming()) {
                IOUtils.copy(backendResponse.getEntity().getContent(), response.getOutputStream());
                response.flushBuffer();
            }
        } finally {
            backendResponse.close();
            response.getOutputStream().close();
        }
    }

    @HEAD
    @Path("{subResources:.*}")
    @Timed
    public void proxyHead(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        LOG.info("proxy HEAD request '{}', '{}'", request.getRequestURI(), request.getQueryString());

        String url = "http://" + chooseBackendInstance() + request.getRequestURI();
        if (!StringUtils.isEmpty(request.getQueryString())) {
            url += request.getQueryString();
        }

        HttpHead delegate = new HttpHead(url);

        final CloseableHttpResponse backendResponse;
        try {
            backendResponse = httpClient.execute(delegate);
        } catch (IOException e) {
            LOG.warn("502 Bad Gateway '{}'", e.getMessage());
            response.setContentType("text/plain");
            response.getOutputStream().print("502 Bad Gateway");
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.flushBuffer();
            return;
        }

        try {
            // copy headers to response
            for (Header header : backendResponse.getAllHeaders()) {
                if ("Connection".equals(header.getName())) {
                    continue;
                }
                response.setHeader(header.getName(), header.getValue());
            }
        } finally {
            backendResponse.close();
            response.getOutputStream().close();
        }
    }

    private String chooseBackendInstance() {
        return "localhost:80";
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
