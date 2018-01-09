package me.giacoppo.remoteconfig;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RemoteConfigSettingsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testExceptions() {
        new RemoteConfigSettings.Builder(null).build();
    }

    @Test
    public void testCorrectness() {
        RemoteConfigSettings s = new RemoteConfigSettings.Builder<>(Object.class).build();
        assertTrue(s != null);
        assertEquals(s.getClassOfConfig().getSimpleName(), Object.class.getSimpleName());
    }
}