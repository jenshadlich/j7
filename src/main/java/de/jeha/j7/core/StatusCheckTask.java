package de.jeha.j7.core;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author jenshadlich@googlemail.com
 */
public class StatusCheckTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(StatusCheckTask.class);

    private final CloseableHttpClient httpClient;
    private final Server server;

    public StatusCheckTask(CloseableHttpClient httpClient, Server server) {
        this.httpClient = httpClient;
        this.server = server;
    }

    @Override
    public void run() {
        LOG.debug("check status for {}", server.getInstance());
        HttpGet statusCheck = new HttpGet(server.getStatusCheckUrl());

        CloseableHttpResponse statusCheckResponse = null;
        try {
            statusCheckResponse = httpClient.execute(statusCheck);

            if (statusCheckResponse.getStatusLine().getStatusCode() == 200) {
                server.getStatus().rise();
            } else {
                server.getStatus().fall();
            }
        } catch (IOException e) {
            LOG.debug("check status failed for {}: {}", server.getInstance(), e.getMessage());
            server.getStatus().fall();
        } finally {
            IOUtils.closeQuietly(statusCheckResponse);
        }
    }

}
