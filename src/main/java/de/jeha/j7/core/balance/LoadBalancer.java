package de.jeha.j7.core.balance;

import de.jeha.j7.core.Server;

/**
 * @author jenshadlich@googlemail.com
 */
public interface LoadBalancer {

    Server balance();

}
