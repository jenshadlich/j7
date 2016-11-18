package de.jeha.j7.core.balance;

import de.jeha.j7.core.Backend;
import de.jeha.j7.core.BackendDownException;
import de.jeha.j7.core.Server;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jenshadlich@googlemail.com
 */
public class RoundRobinTest {

    @Test(expected = BackendDownException.class)
    public void testBackendDown() throws BackendDownException {
        Backend mockedBackend = mock(Backend.class);
        when(mockedBackend.isUp()).thenReturn(false);

        new RoundRobin(mockedBackend).balance();
    }

    @Test
    public void test() throws BackendDownException {
        Backend mockedBackend = mock(Backend.class);
        when(mockedBackend.isUp()).thenReturn(true);
        when(mockedBackend.getServers()).thenReturn(Arrays.asList(new Server("1"), new Server("2")));

        LoadBalancer lb = new RoundRobin(mockedBackend);

        assertEquals("1", lb.balance().getInstance());
        assertEquals("2", lb.balance().getInstance());
        assertEquals("1", lb.balance().getInstance());
    }

}
