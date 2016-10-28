package de.jeha.j7.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.jeha.j7.core.LoadBalancer;
import de.jeha.j7.core.RoundRobin;
import io.dropwizard.Configuration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author jenshadlich@googlemail.com
 */
public class J7Configuration extends Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(J7Configuration.class);

    @JsonProperty
    private String serverSignature;

    @NotNull
    @Valid
    private BackendConfiguration backend;

    @JsonCreator
    public J7Configuration(@JsonProperty("backend") BackendConfiguration backend) {
        this.backend = backend;
    }

    public String getServerSignature() {
        return serverSignature;
    }

    public BackendConfiguration getBackend() {
        return backend;
    }

    public LoadBalancer buildLoadBalancer() {
        LOG.info("build round-robin loadbalancer");
        return new RoundRobin(backend.getServers());
    }

    public CloseableHttpClient buildHttpClient() {
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
