package com.maynooth.zcc.ui;

import com.maynooth.zcc.zapi.Zapi;
import org.json.JSONObject;

/**
 * @author Markus
 * BeforeState class: A concrete child class of UIState.
 * This state is activated when user choose "previous page" on ticket list display.
 */
public class BeforeState extends UIState {
    /**
     * BeforeState constructor.
     * @param aApi: The Zapi class.
     * @param aController: The controller that holds this class.
     */    
    public BeforeState(Zapi aApi, Controller aController){
        api = aApi;
        controller = aController;
    }
    
    /**
     * sendRequest method: Call Zapi to get the previous page of the ticket list. 
     */
    @Override
    void sendRequest() throws Exception {
        JSONObject response = api.getUsers();
        userMap = mapUserIDToName(response.getJSONArray("users"));

        String beforeCursor = lastTickets.getJSONObject("meta").getString("before_cursor");
        data = api.getTickets(beforeCursor, "before");
        if (data.getJSONArray("tickets").length() == 0){
            System.out.println("There is no previous page");
        }else{
            lastTickets = data;
        }
    }
}
