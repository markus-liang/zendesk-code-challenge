package com.maynooth.zcc.ui;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Markus
 */
public class ControllerTest {
    Controller controller;
    
    public ControllerTest() {
    }
    
    @Before
    public void setUp() {
        controller = new Controller();
    }
    
    @Test
    public void testChangeState() {
        // Check if the default state is InitState
        assertTrue(controller.getState() instanceof InitState);

        // Change to BeforeState
        controller.changeState("BEFORE");
        assertTrue(controller.getState() instanceof BeforeState);

        // Change to AfterState
        controller.changeState("AFTER");
        assertTrue(controller.getState() instanceof AfterState);

        // Change to DetailrState
        controller.changeState("DETAIL");
        assertTrue(controller.getState() instanceof DetailState);
    }    
}
