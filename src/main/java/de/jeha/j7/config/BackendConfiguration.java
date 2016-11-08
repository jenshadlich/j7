package de.jeha.j7.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author jenshadlich@googlemail.com
 */
public class BackendConfiguration {

    @JsonProperty
    private String name;

    @JsonProperty
    private StatusCheckConfiguration statusCheck;

    @JsonProperty
    @Size(min = 1)
    private List<ServerConfiguration> servers;

    public String getName() {
        return name;
    }

    public StatusCheckConfiguration getStatusCheck() {
        return statusCheck;
    }

    public List<ServerConfiguration> getServers() {
        return servers;
    }


}
