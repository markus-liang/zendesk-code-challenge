package com.maynooth.zcc.zapi;

import com.maynooth.zcc.Config;
import com.maynooth.zcc.connection.Connector;
import java.net.http.HttpResponse;
import java.util.Base64;
import org.json.JSONObject;

/**
 * @author markus
 * Zapi class: Handle the Zendesk API request
 */
public class Zapi {
    public static final int minPageSize = 1;
    public static final int maxPageSize = 100;
    private int pageSize = 25; // default pageSize = 25
    private String baseUrl;
    private Connector connector;

    /**
     * Zapi constructor
     * @param aConnector: a Connector class
     */    
    public Zapi(Connector aConnector) {
        Config config = Config.getInstance();
        baseUrl = config.get("API");
        if (baseUrl.charAt(baseUrl.length() - 1) != '/'){
            baseUrl += "/";
        }
        connector = aConnector;
    }
        
    /**
     * getPageSize method: Get current page size
     */    
    public int getPageSize(){
        return pageSize;
    }
    
    /**
     * setPageSize method: Set page size
     * @param aPageSize: The number of ticket will be displayed in a page
     */    
    public void setPageSize(int aPageSize){
        if (aPageSize < minPageSize){
           pageSize = minPageSize;
        } else if (aPageSize > maxPageSize){
            pageSize = maxPageSize;
        } else {
            pageSize = aPageSize;
        }
    }
    
    /**
     * getTicket method: Get detail of a ticket
     * @param aID: The ticket id
     */    
    public JSONObject getTicket(long aID) throws Exception {
        connector.setUri(baseUrl + "tickets/" + aID + ".json");
        return connector.getResponse();
    }

    /**
     * getTicketList method: Get ticket list
     * @param aParams: Query parameters
     */    
    private JSONObject getTicketList(String aParams) throws Exception {
        connector.setUri(baseUrl + "tickets.json?" + aParams);
        return connector.getResponse();
    }
    
    /**
     * getTickets method: The public method to get list of tickets
     * @param aCursor: Cursor to next/previous page
     * @param aCursorType: Cursor type [after|before]
     */    
    public JSONObject getTickets(String aCursor, String aCursorType) throws Exception {
        String paramStr = "page[size]=" + pageSize + "&sort=-updated_at";
        if (!aCursorType.equals("")){
            paramStr = "page[" + aCursorType + "]=" + aCursor + "&" + paramStr;
        }        
        return getTicketList(paramStr);
    }
    
    /**
     * getUsers method: Get list of users
     */    
    public JSONObject getUsers() throws Exception {
        connector.setUri(baseUrl + "users.json");
        return connector.getResponse();
    }
    
    /**
     * getLastStatusCode method: Get the last request status code
     */    
    public int getLastStatusCode(){
        return connector.getLastStatusCode();
    }
}
