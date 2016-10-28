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
    @Size(min = 1)
    private List<BackendServer> servers;

    public String getName() {
        return name;
    }

    public List<BackendServer> getServers() {
        return servers;
    }

}
