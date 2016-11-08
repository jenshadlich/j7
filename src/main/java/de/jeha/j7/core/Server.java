package de.jeha.j7.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jenshadlich@googlemail.com
 */
public class Server {

    private final String instance;
    private final String statusCheckPath;
    private final Status status;

    public Server(String instance, String statusCheckPath, Status status) {
        this.instance = instance;
        this.statusCheckPath = statusCheckPath;
        this.status = status;
    }

    public String getInstance() {
        return instance;
    }

    public String getStatusCheckPath() {
        return statusCheckPath;
    }

    public Status getStatus() {
        return status;
    }

    String getStatusCheckUrl() {
        return "http://" + instance + statusCheckPath;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public static class Status {

        private static final Logger LOG = LoggerFactory.getLogger(Status.class);

        private OpState opState = OpState.UP;

        void rise() {
            LOG.debug("rise");
            opState = OpState.UP;
        }

        void fall() {
            LOG.debug("fall");
            opState = OpState.DOWN;
        }

        public OpState getOpState() {
            return opState;
        }

    }

}