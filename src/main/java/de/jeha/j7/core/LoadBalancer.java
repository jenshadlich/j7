package de.jeha.j7.core;

import de.jeha.j7.config.BackendServer;

/**
 * @author jenshadlich@googlemail.com
 */
public interface LoadBalancer {

    BackendServer balance();

}
