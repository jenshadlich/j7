package de.jeha.j7.core.balance;

import de.jeha.j7.core.Backend;
import de.jeha.j7.core.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author jenshadlich@googlemail.com
 */
public class RoundRobin implements LoadBalancer {

    private static final Logger LOG = LoggerFactory.getLogger(RoundRobin.class);

    private final CircularList<Server> servers;

    public RoundRobin(List<Server> servers) {
        this.servers = new CircularList<>(servers);
    }

    @Override
    public Server balance() {
        final Server instance = servers.next();
        LOG.debug("balance: {}", instance);
        return instance;
    }

    private static class CircularList<T> {
        private int i = 0;
        private final List<T> items;

        CircularList(List<T> items) {
            this.items = items;
        }

        synchronized T next() {
            i++;
            if (i >= items.size()) {
                i = 0;
            }
            return items.get(i);
        }
    }

}

