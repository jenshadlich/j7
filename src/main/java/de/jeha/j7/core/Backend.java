package de.jeha.j7.core;

import de.jeha.j7.core.balance.LoadBalancer;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;

/**
 * @author jenshadlich@googlemail.com
 */
public class Backend {

    private final String name;
    private final List<Server> servers;
    private final LoadBalancer loadBalancer;
    private final CloseableHttpClient httpClient;

    public Backend(String name, List<Server> servers, LoadBalancer loadBalancer, CloseableHttpClient httpClient) {
        this.name = name;
        this.servers = servers;
        this.loadBalancer = loadBalancer;
        this.httpClient = httpClient;
    }

    public String getName() {
        return name;
    }

    public List<Server> getServers() {
        return servers;
    }

    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * @return true if at least one server is up.
     */
    public boolean isUp() {
        for (Server server : servers) {
            if (server.getStatus().getOpState().isUp()) {
                return true;
            }
        }
        return false;
    }

}
