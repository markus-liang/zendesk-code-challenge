package com.maynooth.zcc.ui;

import com.maynooth.zcc.zapi.Zapi;
import org.json.JSONObject;

/**
 * @author Markus
 * DetailState class: A concrete child class of UIState.
 * This state is activated when user choose "ticket detail" on ticket list display.
 */
public class DetailState extends UIState {
    private long ticketID;
    
    /**
     * DetailState constructor.
     * @param aApi: The Zapi class.
     * @param aController: The controller that holds this class.
     */    
    public DetailState(Zapi aApi, Controller aController){
        api = aApi;
        controller = aController;
    }
    
    /**
     * run method: Override the run method of the parent.
     * Need to ask ticket ID from user.
     */
    void run(){
        try
        {
            askTicketID();
            sendRequest();
            showTicket();
            showMenu();
            askUserInput();
        }catch(Exception e){
            System.out.println("Failed processing request with status code: "
                    + api.getLastStatusCode()
                    + ". Please contact your administrator.");
            controller.changeState("EXIT");
        }
    }
    
    /**
     * showMenu method: Override the showMenu method of the parent.
     * In this state, the available menu is change.
     */
    void showMenu(){
        numberOfMenu = 2;
        
        System.out.println("\n==========  Zendesk Ticketing ==========\n");

        System.out.println("0. Close application");
        System.out.println("1. Back to list");
        System.out.println("2. View another ticket\n");
    }

    /**
     * sendRequest method: Call Zapi to get the ticket details. 
     */    
    void sendRequest() throws Exception {
        JSONObject response = api.getUsers();
        userMap = mapUserIDToName(response.getJSONArray("users"));

        data = api.getTicket(ticketID);
    }

    /**
     * askTicketID method: Ask user to enter the ticket ID.
     */    
    void askTicketID(){
        boolean isValid = false;
        do{
            try{
                ticketID = Long.parseLong(getInput("Input ticket ID: ").trim());
                isValid = true;
            }catch(Exception e){
                System.out.println("Ticket ID must be Number.");
            }
        }while(!isValid);
    }
    
    /**
     * callChangeState method: Tell the controller to change the UI state.
     */    
    void callChangeState(int aInput){
        switch(aInput){
            case 0: controller.changeState("EXIT");
                break;
            case 1: controller.changeState("INIT");
                break;
            case 2: controller.changeState("DETAIL");
                break;
        }
    }
}
