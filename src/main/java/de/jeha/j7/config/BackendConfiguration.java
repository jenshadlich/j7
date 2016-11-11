package de.jeha.j7.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.jeha.j7.core.balance.LoadBalancer;
import de.jeha.j7.core.balance.LoadBalancerFactory;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author jenshadlich@googlemail.com
 */
class BackendConfiguration {

    @JsonProperty
    private String name;

    @JsonProperty
    private LoadBalancerConfiguration loadBalancer;

    @JsonProperty
    private StatusCheckConfiguration statusCheck;

    @JsonProperty
    @Size(min = 1)
    private List<ServerConfiguration> servers;

    public String getName() {
        return name;
    }

    public LoadBalancerConfiguration getLoadBalancer() {
        return loadBalancer;
    }

    public StatusCheckConfiguration getStatusCheck() {
        return statusCheck;
    }

    public List<ServerConfiguration> getServers() {
        return servers;
    }


}
