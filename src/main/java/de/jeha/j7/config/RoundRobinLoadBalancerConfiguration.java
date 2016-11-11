package de.jeha.j7.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.jeha.j7.core.balance.LoadBalancerFactory;
import de.jeha.j7.core.balance.RoundRobin;

/**
 * @author jenshadlich@googlemail.com
 */
@JsonTypeName("round-robin")
class RoundRobinLoadBalancerConfiguration implements LoadBalancerConfiguration {

    @Override
    public LoadBalancerFactory getFactory() {
        return RoundRobin::new;
    }

}
