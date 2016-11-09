package de.jeha.j7.core.balance;

import de.jeha.j7.core.Backend;
import de.jeha.j7.core.BackendDownException;
import org.junit.Test;

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

}
