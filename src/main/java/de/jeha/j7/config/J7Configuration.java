package de.jeha.j7.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.jeha.j7.core.Backend;
import de.jeha.j7.core.Server;
import de.jeha.j7.core.balance.RoundRobin;
import io.dropwizard.Configuration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jenshadlich@googlemail.com
 */
public class J7Configuration extends Configuration {

    @NotNull
    @JsonProperty
    private String serverSignature;

    @NotNull
    @Valid
    private BackendConfiguration backendConfiguration;

    private transient Backend backend;

    @JsonCreator
    public J7Configuration(@JsonProperty("backend") BackendConfiguration backendConfiguration) {
        this.backendConfiguration = backendConfiguration;
    }

    public String getServerSignature() {
        return serverSignature;
    }

    public Backend getBackend() {
        if (backend == null) {
            backend = buildBackend();
        }
        return backend;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private Backend buildBackend() {
        final List<Server> servers = backendConfiguration
                .getServers()
                .stream()
                .map(serverConfiguration ->
                        new Server(
                                serverConfiguration.getInstance(),
                                backendConfiguration.getStatusCheck().getPath(),
                                new Server.Status()))
                .collect(Collectors.toList());

        return new Backend(
                backendConfiguration.getName(),
                servers,
                RoundRobin::new,
                buildHttpClient());
    }

    private CloseableHttpClient buildHttpClient() {
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(10_000)
                .setSocketTimeout(10_000)
                .setRedirectsEnabled(false)
                .build();

        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(1024);

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config)
                .build();
    }

    public CloseableHttpClient buildStatusCheckHttpClient() {
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(1_000)
                .setSocketTimeout(1_000)
                .setRedirectsEnabled(false)
                .setContentCompressionEnabled(false)
                .build();

        return HttpClientBuilder.create()
                .setConnectionManager(new BasicHttpClientConnectionManager())
                .setDefaultRequestConfig(config)
                .setUserAgent(backendConfiguration.getStatusCheck().getUserAgent())
                .build();
    }

}
