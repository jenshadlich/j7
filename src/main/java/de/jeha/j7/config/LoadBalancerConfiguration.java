package de.jeha.j7.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.jeha.j7.core.balance.LoadBalancerFactory;
import io.dropwizard.jackson.Discoverable;

/**
 * @author jenshadlich@googlemail.com
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
interface LoadBalancerConfiguration extends Discoverable {
    LoadBalancerFactory getFactory();
}
