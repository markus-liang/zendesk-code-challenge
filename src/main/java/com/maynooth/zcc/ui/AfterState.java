package com.maynooth.zcc.ui;

import com.maynooth.zcc.zapi.Zapi;
import org.json.JSONObject;

/**
 * @author Markus
 * AfterState class: A concrete child class of UIState.
 * This state is activated when user choose "next page" on ticket list display.
 */
public class AfterState extends UIState{
    /**
     * AfterState constructor.
     * @param aApi: The Zapi class.
     * @param aController: The controller that holds this class.
     */
    public AfterState(Zapi aApi, Controller aController){
        api = aApi;
        controller = aController;
    }

    /**
     * sendRequest method: Call Zapi to get the next page of the ticket list.
     */
    @Override
    void sendRequest() throws Exception {
        if (!lastTickets.getJSONObject("meta").getBoolean("has_more")){
            System.out.println("There is no more tickets available.");
        }else{
            JSONObject response = api.getUsers();
            userMap = mapUserIDToName(response.getJSONArray("users"));

            String afterCursor = lastTickets.getJSONObject("meta").getString("after_cursor");
            data = api.getTickets(afterCursor, "after");
            lastTickets = data;
        }        
    }
}
