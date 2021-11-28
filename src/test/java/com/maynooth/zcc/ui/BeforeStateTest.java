package com.maynooth.zcc.ui;

import static com.maynooth.zcc.ui.UIState.lastTickets;
import com.maynooth.zcc.zapi.MockData;
import com.maynooth.zcc.zapi.Zapi;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Markus
 */
public class BeforeStateTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    Zapi api  = mock (Zapi.class);
    Controller controller;
    UIState initState, afterState, beforeState;
        
    public BeforeStateTest() {
    }
    
    @Before
    public void setUp() {
        controller = new Controller();
        initState = new InitState(api, controller);
        afterState = new AfterState(api, controller);
        beforeState = new BeforeState(api, controller);
    }

    @Test
    public void testShowMenu() { // BeforeState has the same menu as default
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
        // run initState and change state to afterState
        when(api.getUsers()).thenReturn(new JSONObject(MockData.getUsers));
        when(api.getTickets("","")).thenReturn(new JSONObject(MockData.getTickets));        
        // First call of InitState.sendRequest is required for all other Sta 
        initState.sendRequest();
        
        // change state to afterState
        initState.callChangeState(2);

        // use MockData.getTickets
        when(
            api.getTickets(lastTickets.getJSONObject("meta").getString("after_cursor"), "after")
        ).thenReturn(new JSONObject(MockData.getTickets));
        // call AfterState.sendRequest 
        afterState.sendRequest();

        // lastTickets = data
        assertEquals(afterState.data, afterState.lastTickets);
        // cek tickets
        assertEquals(2, afterState.data.getJSONArray("tickets").length());
        
        // change state to beforeState
        afterState.callChangeState(1);

        // use MockData.getTickets
        when(
            api.getTickets(lastTickets.getJSONObject("meta").getString("before_cursor"), "before")
        ).thenReturn(new JSONObject(MockData.getTickets));
        // call AfterState.sendRequest 
        beforeState.sendRequest();

        // lastTickets = data
        assertEquals(beforeState.data, beforeState.lastTickets);
        // cek tickets
        assertEquals(2, beforeState.data.getJSONArray("tickets").length());
        
        verify(api, times(3)).getUsers();
        verify(api).getTickets("", "");
        verify(api).getTickets(lastTickets.getJSONObject("meta").getString("after_cursor"), "after");
        verify(api).getTickets(lastTickets.getJSONObject("meta").getString("before_cursor"), "before");
    }    
}
