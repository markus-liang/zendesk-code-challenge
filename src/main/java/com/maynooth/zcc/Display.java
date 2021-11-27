package com.maynooth.zcc;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author markus
 * Display class used display system output to the user
 */
public class Display {
    /**
     * showMenu method: display the system navigation
     */    
    public void showMenu(){
        System.out.println("\n==========  Zendesk Ticketing ==========\n");

        System.out.println("0. Close application");
        System.out.println("1. Prev page");
        System.out.println("2. Next page");
        System.out.println("3. View a ticket\n");
        
        System.out.print("Input menu number : ");
    }
    
    /**
     * showList method: display the list of tickets per page
     * @param aArrTickets : contains the list of ticket
     * @param aUsers : a HashMap of user_id as key to username as value.
     */    
    public void showList(JSONArray aArrTickets, HashMap<String, String> aUsers){
        System.out.println(
            String.format("|%-5s|%-50s|%-20s|%-30s|%-10s|", 
            "ID", "Subject", "Requester", "Requested", "Priority"));
        
        for (int i=0; i<aArrTickets.length(); i++){
            JSONObject obj = (JSONObject)aArrTickets.get(i);

            String subject = obj.get("subject").toString();            
            if(subject.length() >= 45){
                subject = subject.substring(0, 45) + " ...";
            }
            String priority = obj.get("priority") == null ? "" : obj.get("priority").toString();

            String requester = aUsers.get(obj.getBigInteger("requester_id").toString());
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
     * showTicket method: display a single ticket in detail
     * @param aTicket : contains the list of ticket
     * @param aUsers : a HashMap of user_id as key to username as value.
     */    
    public void showTicket(JSONObject aTicket, HashMap<String, String> aUsers){
       System.out.println("\n=== Ticket Detail ===\n");
       System.out.println(String.format("%-20s", "Status") + ":" + aTicket.getString("status"));
       System.out.println(String.format("%-20s", "Priority") + ":" + (aTicket.get("priority") == null ? "" : aTicket.get("priority").toString()));
       System.out.println(String.format("%-20s", "Subject") + ":" + aTicket.getString("subject"));
       System.out.println(String.format("%-20s", "Created at") + ":" + aTicket.getString("created_at"));
       System.out.println(String.format("%-20s", "Created by") + ":" + aUsers.get(aTicket.getBigInteger("submitter_id").toString()));
       System.out.println("\nDescription");
       System.out.println("Subject: " + aTicket.getString("description"));
       
       System.out.println("");
    }
    
    /**
     * message method: display a message to the User
     * @param aMessage : the message to be displayed
     */    
    public void message(String aMessage){
        System.out.println(aMessage);
    }
}
