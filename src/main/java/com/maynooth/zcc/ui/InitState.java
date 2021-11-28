package com.maynooth.zcc.ui;

import com.maynooth.zcc.zapi.Zapi;
import org.json.JSONObject;

/**
 * @author Markus
 * InitState class: A concrete child class of UIState.
 * This state is activated when the application starts or user choose to view ticket from detail state.
 */
class InitState extends UIState {
    /**
     * InitState constructor.
     * @param aApi: The Zapi class.
     * @param aController: The controller that holds this class.
     */    
    public InitState(Zapi aApi, Controller aController){
        api = aApi;
        controller = aController;
    }
    
    /**
     * sendRequest method: Call Zapi to get the the ticket list.
     */
    @Override
    void sendRequest() throws Exception {
        JSONObject response = api.getUsers();
        userMap = mapUserIDToName(response.getJSONArray("users"));            
        data = api.getTickets("", "");
        lastTickets = data;
    }
}
