package de.jeha.j7.health;

import com.codahale.metrics.health.HealthCheck;
import de.jeha.j7.core.Server;

/**
 * @author jenshadlich@googlemail.com
 */
public class ServerHealthCheck extends HealthCheck {

    private final Server server;

    public ServerHealthCheck(Server server) {
        this.server = server;
    }

    @Override
    protected Result check() throws Exception {
        return server.getStatus().getOpState().isUp()
                ? Result.healthy("[instance=" + server.getInstance() + "] is UP")
                : Result.unhealthy("[instance=" + server.getInstance() + "] is DOWN");
    }

}
