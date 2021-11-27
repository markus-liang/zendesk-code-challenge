package com.maynooth.zcc.connection;

import com.maynooth.zcc.Config;
import com.maynooth.zcc.zapi.MockData;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Markus
 */
public class ConnectorTest {
    private Connector connector;
    private String baseUrl;
    private String auth;
    private String urlString;
    
    public ConnectorTest() {
    }

    @Before
    public void setUp() {
        Config config = Config.getInstance();
        baseUrl = config.get("API");
        urlString = baseUrl + "tickets/100.json";
        
        if (baseUrl.charAt(baseUrl.length() - 1) != '/'){
            baseUrl += "/";
        }
        auth = "Basic " + 
                Base64.getEncoder().encodeToString((config.get("USERNAME") + ":" + config.get("PASSWORD")).getBytes());        
    }

    @Test
    public void testSetAndGetUri() {
        URI expectedUri = URI.create(urlString);
        HttpClient httpClient = mock(HttpClient.class); // mocked httpClient

        connector = new Connector(auth, httpClient);
        connector.setUri(urlString);

        // getUri should return an instance of URI
        assertTrue(connector.getUri() instanceof URI);
        assertEquals(0, connector.getUri().compareTo(expectedUri));
    }

    @Test
    public void testGetResponse() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class); // mocked HttpResponse

        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(urlString))
            .setHeader("Authorization" , auth)
            .build();
        
        HttpClient httpClient = mock(HttpClient.class); // mocked httpClient
        when(httpClient.send(request, HttpResponse.BodyHandlers.ofString())).thenReturn(response);
        
        //create connector with mocked httpClient
        connector = new Connector(auth, httpClient);
        connector.setUri(urlString);
                
        // successful request
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(MockData.getTicket);

        JSONObject jsonResponse = connector.getResponse();
        assertEquals(200, connector.getLastStatusCode());
        assertEquals(new BigInteger("100"), jsonResponse.getJSONObject("ticket").getBigInteger("id"));
           
        // API not found
        when(response.statusCode()).thenReturn(400);
        when(response.body()).thenReturn("");

        jsonResponse = connector.getResponse();
        assertEquals(400, connector.getLastStatusCode());
        assertNull(jsonResponse);
        
        verify(httpClient, times(2)).send(request, HttpResponse.BodyHandlers.ofString());
    }    
}
