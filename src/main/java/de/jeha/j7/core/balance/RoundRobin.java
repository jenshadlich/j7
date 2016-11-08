package de.jeha.j7.core.balance;

import de.jeha.j7.core.Backend;
import de.jeha.j7.core.BackendDownException;
import de.jeha.j7.core.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author jenshadlich@googlemail.com
 */
public class RoundRobin implements LoadBalancer {

    private static final Logger LOG = LoggerFactory.getLogger(RoundRobin.class);

    private final Backend backend;
    private final CircularList<Server> servers;

    public RoundRobin(Backend backend) {
        this.backend = backend;
        this.servers = new CircularList<>(backend.getServers());
    }

    @Override
    public Server balance() throws BackendDownException {
        LOG.debug("Race for the next healthy server ...");

        while (backend.isUp()) {
            final Server server = servers.next();
            if (server.getStatus().getOpState().isUp()) {
                LOG.debug("Found server {}", server);
                return server;
            }
        }

        throw new BackendDownException("[backend=" + backend.getName() + "] is DOWN");
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

