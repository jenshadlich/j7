package de.jeha.j7.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jenshadlich@googlemail.com
 */
class ServerConfiguration {

    @JsonProperty
    private String instance;

    public String getInstance() {
        return instance;
    }

}
