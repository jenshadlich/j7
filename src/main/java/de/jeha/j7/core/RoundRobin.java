package de.jeha.j7.core;

import de.jeha.j7.config.BackendServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author jenshadlich@googlemail.com
 */
public class RoundRobin implements LoadBalancer {

    private static final Logger LOG = LoggerFactory.getLogger(RoundRobin.class);

    private final CircularList<BackendServer> servers;

    public RoundRobin(List<BackendServer> servers) {
        this.servers = new CircularList<>(servers);
    }

    @Override
    public BackendServer balance() {
        final BackendServer instance = servers.next();
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

