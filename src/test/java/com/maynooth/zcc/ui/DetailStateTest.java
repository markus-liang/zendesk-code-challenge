package com.maynooth.zcc.ui;

import com.maynooth.zcc.zapi.MockData;
import com.maynooth.zcc.zapi.Zapi;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.json.JSONObject;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Markus
 */
public class DetailStateTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    Zapi api  = mock (Zapi.class);
    Controller controller;
    DetailState detailState;
    
    public DetailStateTest() {
    }

    @Before
    public void setUp() {
        controller = new Controller();
        detailState = new DetailState(api, controller);
    }

    @Test
    public void testShowMenu() {
        System.setOut(new PrintStream(outputStreamCaptor));

        // Add the extra new line char at the end, because the use of .println
        String expected = "\n==========  Zendesk Ticketing ==========\n\n";
        expected += "0. Close application\n";
        expected += "1. Back to list\n";
        expected += "2. View another ticket\n\n";

        detailState.showMenu();
        Assert.assertEquals(expected, outputStreamCaptor.toString());
    }

    @Test
    public void testSendRequest() throws Exception {
        when(api.getUsers()).thenReturn(new JSONObject(MockData.getUsers));
        when(api.getTicket(100)).thenReturn(new JSONObject(MockData.getTicket));
        // First call of InitState.sendRequest is required for all other State
        detailState.ticketID = 100;        
        detailState.sendRequest();
        
        assertTrue(detailState.data instanceof JSONObject);
        JSONObject ticket = detailState.data.getJSONObject("ticket");
        assertEquals(100L, ticket.getLong("id"));
        assertEquals("adipisicing duis quis consequat velit", ticket.getString("subject"));
        assertEquals(388792560458L, ticket.getLong("submitter_id"));
        assertEquals("2021-11-20T10:39:48Z", ticket.getString("created_at"));
        
        verify(api).getUsers();
        verify(api).getTicket(100);
    }

    @Test
    public void testCallChangeState() {
        // test InitialState
        assertTrue(controller.getState() instanceof InitState);
        // change to DetailState
        detailState.callChangeState(2);
        assertTrue(controller.getState() instanceof DetailState);
        // change back to InitialState
        detailState.callChangeState(1);
        assertTrue(controller.getState() instanceof InitState);
        // exit
        detailState.callChangeState(0);
        assertNull(controller.getState());
    }
    
}
