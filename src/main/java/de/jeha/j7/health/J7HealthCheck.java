package de.jeha.j7.health;

import com.codahale.metrics.health.HealthCheck;
import de.jeha.j7.core.Backend;

/**
 * @author jenshadlich@googlemail.com
 */
public class J7HealthCheck extends HealthCheck {

    private final Backend backend;

    public J7HealthCheck(Backend backend) {
        this.backend = backend;
    }

    @Override
    protected Result check() throws Exception {
        return backend.isUp()
                ? Result.healthy("backend is UP")
                : Result.unhealthy("backend is DOWN");
    }

}
