package com.maynooth.zcc;

import com.maynooth.zcc.connection.Connector;
import com.maynooth.zcc.zapi.Zapi;
import java.net.http.HttpClient;
import java.util.Base64;
import java.util.Scanner;
import java.util.HashMap;
import java.util.InputMismatchException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author markus
 * Main class: the main class of this project
 */
public class Main {
    private static Zapi api;
    private static Display display = new Display();
    private static Scanner inScanner = new Scanner(System.in);
    private static HashMap<String, String> userMap;
    private static JSONObject ticket = null;
    private static JSONObject tickets = null;
    private static boolean hasPreviousPage;
    
    /**
     * mapUserIDToName method: produce a HashMap of user id to user name
     * @param aArr: JSONArray of users
     */
    private static HashMap<String, String> mapUserIDToName(JSONArray aArr){
        HashMap<String, String> users = new HashMap<String, String>();
        
        for (int i=0; i<aArr.length(); i++){
            JSONObject obj = (JSONObject)aArr.get(i);
            if(!users.containsKey(obj.getBigInteger("id").toString())){
                users.put(obj.getBigInteger("id").toString(), obj.getString("name"));
            }
        }
        return users;
    }
    
    /**
     * sendRequest method: call the Zendesk API (Zapi) based on the request type
     * @param aReq: the API type to be called
     * @param arg: the argument for the API
     */
    private static JSONObject sendRequest(String aReq, String arg) throws Exception {
        JSONObject response = null;
        switch(aReq){
            case "users":
                response = api.getUsers();
                break;
            case "tickets":
                response = api.getTickets("", "");
                break;
            case "tickets-after":
                response = api.getTickets(arg, "after");
                break;
            case "tickets-before":
                response = api.getTickets(arg, "before");
                break;
            case "ticket":
                response = api.getTicket(Long.parseLong(arg));
                break;
        }
        
        if(response == null){
            throw new Exception(
                    "Request API failed with status code "  + 
                    api.getLastStatusCode() + ", please contact Zendesk helpdesk.");
        }
        return response;
    }
    
    /**
     * processUserInput method: process user input
     * @param aInput: the menu selected by the user
     */
    public static void processUserInput(int aInput) throws Exception {
        int ticketID;

        switch (aInput) {
            case 1: // user input: previous
                if (!hasPreviousPage){
                    display.message("There is no previous page.");
                    break;
                }

                tickets = sendRequest("tickets-before", tickets.getJSONObject("meta").getString("before_cursor"));
                if (tickets != null){
                    display.showList(tickets.getJSONArray("tickets"), userMap);
                    hasPreviousPage = (tickets.getJSONArray("tickets").length() > 0);
                }
                
                break;
            case 2: // user input: next
                if (!tickets.getJSONObject("meta").getBoolean("has_more")){
                    display.message("No more page.");
                    break;
                }
                
                tickets = sendRequest("tickets-after", tickets.getJSONObject("meta").getString("after_cursor"));
                if (tickets != null){
                    display.showList(tickets.getJSONArray("tickets"), userMap);
                    hasPreviousPage = true;
                }
                
                break;
            case 3: // user input: view a ticket
                display.message("Input ticket ID:");
                ticketID = inScanner.nextInt();
                ticket = sendRequest("ticket", "" + ticketID);
                if (tickets != null){
                    display.showTicket(ticket.getJSONObject("ticket"), userMap);
                }
                
                break;
        }
    }
    
    /**
     * main method: the entry point of the application
     */
    public static void main(String[] args){        
        Config config = Config.getInstance();
        String auth = "Basic " + 
                Base64.getEncoder().encodeToString((config.get("USERNAME") + ":" + config.get("PASSWORD")).getBytes());
        HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

        api = new Zapi(new Connector(auth, httpClient));
                
        try
        {
            // get map of user ID to Name
            JSONObject users = sendRequest("users", "");
            userMap = mapUserIDToName(users.getJSONArray("users"));
                        
            // display ticket list at the first run           
            tickets = sendRequest("tickets", "");
            display.showList(tickets.getJSONArray("tickets"), userMap);
            hasPreviousPage = false;

            int input;
            do{
                try
                {
                    display.showMenu();
                    input = inScanner.nextInt();
                    
                    processUserInput(input);
                }catch(InputMismatchException e){
                    display.message("Invalid Input");
                    input = -1;
                    inScanner.nextLine();
                }
            }while(input != 0);
        }catch(Exception ex){
            display.message("Oops.. something went wrong: " + ex.getMessage());
        }
    }
}
