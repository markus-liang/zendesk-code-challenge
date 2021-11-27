package com.maynooth.zcc.connection;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

/**
 * @author markus
 * Connector class: Handle the HTTP request
 */
public class Connector {
    private HttpClient httpClient;
    private URI uri;
    private String auth;
    private HttpResponse<String> lastResponse;
    
    /**
     * Connector constructor
     * @param aAuth: Base64 authentication string
     * @param aHttpClient: a HttpClient class
     */    
    public Connector(String aAuth, HttpClient aHttpClient){
        httpClient = aHttpClient;
        auth = aAuth;
    }

    /**
     * setUri method: Set request address
     * @param aUrl: the web address
     */    
    public void setUri(String aUrl){
        uri = URI.create(aUrl);
    }
    
    /**
     * getUri method: Get the request address
     */    
    public URI getUri(){
        return uri;
    }

    /**
     * getResponse method: Execute the HTTP request
     */    
    public JSONObject getResponse() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .setHeader("Authorization" , auth)
            .build();
        
        lastResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = null;
        if (lastResponse.statusCode() == 200){
            json = new JSONObject(lastResponse.body());
        }
        
        return json;
    }
    
    /**
     * getLastStatusCode method: return the status code from the last request
     */    
    public int getLastStatusCode(){
        if (lastResponse == null) {
            return -1;
        }

        return lastResponse.statusCode();
    }
}
