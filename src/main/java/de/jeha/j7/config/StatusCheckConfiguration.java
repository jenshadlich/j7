package de.jeha.j7.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author jenshadlich@googlemail.com
 */
class StatusCheckConfiguration {

    @JsonProperty
    private String path;

    @JsonProperty
    private String userAgent = "j7";

    public String getPath() {
        return path;
    }

    public String getUserAgent() {
        return userAgent;
    }

}
