package de.jeha.j7.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jenshadlich@googlemail.com
 */
public class BackendServer {

    @JsonProperty
    private String instance;

    public String getInstance() {
        return instance;
    }

}