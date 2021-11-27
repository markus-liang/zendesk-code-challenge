package com.maynooth.zcc;

import java.util.Hashtable;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Markus
 */
public class ConfigTest {
    Config config;
    
    public ConfigTest() {
    }
    
    @Before
    public void setUp() {
        config = Config.getInstance();
    }

    @Test
    public void testGetInstance() {
        // check config instance
        assertTrue(config instanceof Config);
    }

    @Test
    public void testGet() {
        // the check the existing settings, no test the config value
        assertNotNull(config.get("API"));
        assertNotNull(config.get("USERNAME"));
        assertNotNull(config.get("PASSWORD"));
    }
    
}
