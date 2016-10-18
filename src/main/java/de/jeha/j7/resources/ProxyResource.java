package de.jeha.j7.resources;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jenshadlich@googlemail.com
 */
@RestController
public class ProxyResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyResource.class);

    @Autowired
    private CloseableHttpClient httpClient;

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public void proxyGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    @RequestMapping(value = "/**", method = RequestMethod.HEAD)
    public void proxyHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        return "localhost:8888";
    }

}
