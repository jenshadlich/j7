package de.jeha.j7.core.balance;

import de.jeha.j7.core.Backend;
import de.jeha.j7.core.BackendDownException;
import de.jeha.j7.core.Server;

/**
 * @author jenshadlich@googlemail.com
 */
public interface LoadBalancerFactory {

    LoadBalancer create(Backend backend);

}
