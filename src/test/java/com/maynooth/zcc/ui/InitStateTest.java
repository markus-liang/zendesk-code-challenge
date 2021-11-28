package com.maynooth.zcc.ui;

import com.maynooth.zcc.zapi.MockData;
import com.maynooth.zcc.zapi.Zapi;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Markus
 */
public class InitStateTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    
    Zapi api  = mock (Zapi.class);
    Controller controller;
    UIState initState;

    public InitStateTest() {
    }

    @Before
    public void setUp() {
        controller = new Controller();
        initState = new InitState(api, controller);
    }
    
    @Test
    public void testShowMenu() { // InitState has the same menu as default
        System.setOut(new PrintStream(outputStreamCaptor));

        // Add the extra new line char at the end, because the use of .println
        String expected = "\n==========  Zendesk Ticketing ==========\n\n";
        expected += "0. Close application\n";
        expected += "1. Prev page\n";
        expected += "2. Next page\n";
        expected += "3. View a ticket\n\n";

        initState.showMenu();
        assertEquals(expected, outputStreamCaptor.toString());
    }
    
    @Test
    public void testSendRequest() throws Exception {
        when(api.getUsers()).thenReturn(new JSONObject(MockData.getUsers));
        when(api.getTickets("","")).thenReturn(new JSONObject(MockData.getTickets));
        
        // call sendRequest
        initState.sendRequest();

        // lastTickets = data
        assertEquals(initState.data, initState.lastTickets);
        // cek meta
        assertTrue(initState.data.getJSONObject("meta").getBoolean("has_more"));
        // cek tickets
        assertEquals(2, initState.data.getJSONArray("tickets").length());
        
        verify(api).getUsers();
        verify(api).getTickets("", "");
    }
    
}
