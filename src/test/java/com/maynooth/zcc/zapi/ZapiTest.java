package com.maynooth.zcc.zapi;

import com.maynooth.zcc.connection.Connector;
import java.math.BigInteger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

/**
 *
 * @author Markus
 */
public class ZapiTest {
    Zapi api;
    Connector connector = mock(Connector.class);
    
    public ZapiTest() {
    }
    
    @Before
    public void setUp() {
        api = new Zapi(connector);
    }

    @Test
    public void testSetPageSize() {
        int expected;
        
        // default size
        expected = 25;
        assertEquals(expected, api.getPageSize());
        
        // change page size
        expected = 15;
        api.setPageSize(expected);
        assertEquals(expected, api.getPageSize());
        
        // change page size below min
        expected = Zapi.minPageSize;
        api.setPageSize(Zapi.minPageSize - 1);
        assertEquals(expected, api.getPageSize());

        // change page size exceeding max
        expected = Zapi.maxPageSize;
        api.setPageSize(Zapi.maxPageSize + 1);
        assertEquals(expected, api.getPageSize());
    }

    @Test
    public void testGetTicket() throws Exception {
        JSONObject mockObject = new JSONObject(MockData.getTicket);
        when(connector.getResponse()).thenReturn(mockObject);
        
        JSONObject response = api.getTicket(100);
        assertTrue(response instanceof JSONObject);
        assertTrue(response.getJSONObject("ticket") instanceof JSONObject);
        assertEquals(new BigInteger("100"), response.getJSONObject("ticket").getBigInteger("id"));
        assertEquals("adipisicing duis quis consequat velit", response.getJSONObject("ticket").getString("subject"));
        assertEquals(new BigInteger("388792560458"), response.getJSONObject("ticket").getBigInteger("submitter_id"));
        assertEquals("2021-11-20T10:39:48Z", response.getJSONObject("ticket").getString("created_at"));        
    }

    @Test
    public void testGetTickets() throws Exception {
        api.setPageSize(2); // reduce number of ticket for test

        JSONObject mockObject = new JSONObject(MockData.getTickets);
        when(connector.getResponse()).thenReturn(mockObject);
        
        // first page / without pointer
        JSONObject response = api.getTickets("", "");
        
        // check the response class
        assertTrue(response instanceof JSONObject);
        
        // check "meta"
        assertTrue(response.getJSONObject("meta") instanceof JSONObject);
        assertTrue(response.getJSONObject("meta").getBoolean("has_more"));
        assertEquals("eyJvIjoiLXVwZGF0ZWRfYXQsLWlkIiwidiI6IlpIVFFtR0VBQUFBQWFXTEdDaWhYQUFBQSJ9", response.getJSONObject("meta").get("after_cursor"));
        assertEquals("eyJvIjoiLXVwZGF0ZWRfYXQsLWlkIiwidiI6IlpHR2ltV0VBQUFBQWFkTEdleWhYQUFBQSJ9", response.getJSONObject("meta").get("before_cursor"));

        // check "tickets"
        assertTrue(response.getJSONArray("tickets") instanceof JSONArray);
        assertEquals(2, response.getJSONArray("tickets").length());
        assertEquals(new BigInteger("101"), response.getJSONArray("tickets").getJSONObject(1).getBigInteger("id"));
        assertEquals("in nostrud occaecat consectetur aliquip", response.getJSONArray("tickets").getJSONObject(1).getString("subject"));

        // get next
        mockObject = new JSONObject(MockData.getTicketsNext);
        when(connector.getResponse()).thenReturn(mockObject);
        response = api.getTickets(response.getJSONObject("meta").getString("after_cursor"), "after");

        // check "next tickets"
        assertTrue(response.getJSONArray("tickets") instanceof JSONArray);
        assertEquals(new BigInteger("100"), response.getJSONArray("tickets").getJSONObject(0).getBigInteger("id"));
        assertEquals("adipisicing duis quis consequat velit", response.getJSONArray("tickets").getJSONObject(0).getString("subject"));

        // go back to previous list
        mockObject = new JSONObject(MockData.getTickets);
        when(connector.getResponse()).thenReturn(mockObject);
        response = api.getTickets(response.getJSONObject("meta").getString("after_cursor"), "after");

        // check "previous tickets"
        assertTrue(response.getJSONArray("tickets") instanceof JSONArray);
        assertEquals(2, response.getJSONArray("tickets").length());
        assertEquals(new BigInteger("101"), response.getJSONArray("tickets").getJSONObject(1).getBigInteger("id"));
        assertEquals("in nostrud occaecat consectetur aliquip", response.getJSONArray("tickets").getJSONObject(1).getString("subject"));
        
        verify(connector, times(3)).getResponse();
    }

    @Test
    public void testGetUsers() throws Exception {
        JSONObject mockObject = new JSONObject(MockData.getUsers);
        when(connector.getResponse()).thenReturn(mockObject);
        JSONObject response = api.getUsers();
        
        // check the response class
        assertTrue(response instanceof JSONObject);

        // check the value
        assertEquals(1, response.getInt("count"));
        assertEquals(1, response.getJSONArray("users").length());
        assertEquals("customer@example.com", response.getJSONArray("users").getJSONObject(0).get("email"));
        verify(connector).getResponse();
    }
}
