package de.jeha.j7.common;

import org.junit.Test;

import java.util.UUID;

import static de.jeha.j7.common.UUIDBase64.uuidToBase64;
import static org.junit.Assert.assertEquals;

/**
 * @author jenshadlich@googlemail.com
 */
public class UUIDBase64Test {

    @Test
    public void test() {
        final UUID uuid = new UUID(0L, 0L);
        assertEquals("AAAAAAAAAAAAAAAAAAAAAA", uuidToBase64(uuid));
    }

}
