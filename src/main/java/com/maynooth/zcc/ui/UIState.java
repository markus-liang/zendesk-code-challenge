package com.maynooth.zcc.ui;

import com.maynooth.zcc.zapi.Zapi;
import java.util.HashMap;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Markus
 * UIState abstract class: A class to handle UI related activities.
 * This class is created to implement state design patterns.
 * The sendRequest method should be implemented in all children class.
 */
abstract class UIState {
    static JSONObject lastTickets = null;
    HashMap<String, String> userMap;
    Controller controller;
    JSONObject data;
    Zapi api;
    int numberOfMenu;
    
    // each state has different request to API
    abstract void sendRequest() throws Exception;

    /**
     * run method: Series of action to be done in a single display.
     */
    void run(){
        try
        {
            sendRequest();
            showTickets();
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
     * mapUserIDToName method: produce a HashMap of user id to user name.
     * @param aArr: JSONArray of users.
     */
    HashMap<String, String> mapUserIDToName(JSONArray aArr){
        HashMap<String, String> users = new HashMap<String, String>();
        
        for (int i=0; i<aArr.length(); i++){
            JSONObject obj = (JSONObject)aArr.get(i);
            if(!users.containsKey(String.valueOf(obj.getLong("id")))){
                users.put(String.valueOf(obj.getLong("id")), obj.getString("name"));
            }
        }
        return users;
    }

    /**
     * showTicket method: display a single ticket in detail.
     */    
    void showTicket(){
        JSONObject ticket = data.getJSONObject("ticket");

        System.out.println("\n=== Ticket Detail ===\n");
        System.out.println(String.format("%-20s", "Status") + ":" + ticket.getString("status"));
        System.out.println(String.format("%-20s", "Priority") + ":" + (ticket.get("priority") == null ? "" : ticket.get("priority").toString()));
        System.out.println(String.format("%-20s", "Subject") + ":" + ticket.getString("subject"));
        System.out.println(String.format("%-20s", "Created at") + ":" + ticket.getString("created_at"));
        System.out.println(String.format("%-20s", "Created by") + ":" + userMap.get(String.valueOf(ticket.getLong("submitter_id"))));
        System.out.println("\nDescription");
        System.out.println("Subject: " + ticket.getString("description"));

        System.out.println("");
    }
    
    /**
     * showTicket method: display a single ticket in detail.
     */    
    void showTickets(){
        JSONArray tickets = data.getJSONArray("tickets");

        System.out.println("\n");
        System.out.println(
            String.format("|%-5s|%-50s|%-20s|%-30s|%-10s|", 
            "ID", "Subject", "Requester", "Requested", "Priority"));
        
        for (int i=0; i<tickets.length(); i++){
            JSONObject obj = (JSONObject)tickets.get(i);

            String subject = obj.get("subject").toString();            
            if(subject.length() >= 45){
                subject = subject.substring(0, 45) + " ...";
            }
            String priority = obj.get("priority") == null ? "" : obj.get("priority").toString();

            String requester = userMap.get(String.valueOf(obj.getLong("requester_id")));
            System.out.println(String.format("|%-5s|%-50s|%-20s|%-30s|%-10s|", 
                obj.get("id").toString(),
                subject,
                requester,
                obj.getString("created_at").toString(),
                priority
            ));
        }
    }
    
    /**
     * getInput method: Read user input.
     * @param aMessage: A description to show to the user.
     */    
    String getInput(String aMessage){
        Scanner scanner = new Scanner(System.in);
        System.out.println(aMessage);
        return scanner.nextLine();
    }

    /**
     * showMenu method: Display the options available for the user.
     */    
    void showMenu(){
        numberOfMenu = 3;
        
        System.out.println("\n==========  Zendesk Ticketing ==========\n");
        System.out.println("0. Close application");
        System.out.println("1. Prev page");
        System.out.println("2. Next page");
        System.out.println("3. View a ticket\n");
    }

    /**
     * askUserInput method: Ask user to choose from the available menu options.
     */    
    void askUserInput(){
        int input = -1;
        boolean isValid = false;
        do{
            try
            {
                input = Integer.parseInt(getInput("Select the options: ").trim());                
                if (input < 0 || input > numberOfMenu){
                    throw new Exception("");
                }

                isValid = true;
            }catch(Exception e){
                System.out.println("Invalid input");
            }
        }while(!isValid);
        
        callChangeState(input);
    }

    /**
     * callChangeState method: Ask controller change the UI state.
     * @param aInput: The selected state value from the user.
     */    
    void callChangeState(int aInput){
        switch(aInput){
            case 0: controller.changeState("EXIT");
                break;
            case 1: controller.changeState("BEFORE");
                break;
            case 2: controller.changeState("AFTER");
                break;
            case 3: controller.changeState("DETAIL");
                break;
        }
    }
}
