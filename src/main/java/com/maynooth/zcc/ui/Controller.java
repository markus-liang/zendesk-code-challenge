package com.maynooth.zcc.ui;

import com.maynooth.zcc.Config;
import com.maynooth.zcc.connection.Connector;
import com.maynooth.zcc.zapi.Zapi;
import java.net.http.HttpClient;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Markus
 * Controller class: A class to control the state of the UI.
 */
public class Controller {
    static Map<String, UIState> stateList = new HashMap<String, UIState>();
    static UIState uiState;
    static Zapi api;
    Config config;

    /**
     * Controller constructor.
     */    
    public Controller() {
        config = Config.getInstance();
        String auth = "Basic " + 
            Base64.getEncoder()
            .encodeToString((config.get("USERNAME") + ":" + config.get("PASSWORD")).getBytes());
        
        HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

        api = new Zapi(new Connector(auth, httpClient));
        
        stateList.put("INIT", new InitState(api, this));
        stateList.put("BEFORE", new BeforeState(api, this));
        stateList.put("AFTER", new AfterState(api, this));
        stateList.put("DETAIL", new DetailState(api, this));
        
        uiState = stateList.get("INIT");
    }
    
    /**
     * start method: Start the operation.
     */    
    public void start(){
        do{
            uiState.run();
        }while(uiState != null);
    }
    
    /**
     * changeState method: Change the UI state.
     * @param aInput: The new state.
     */    
    void changeState(String aInput){
        if(aInput=="EXIT"){
            uiState = null;
        }else{
            uiState = stateList.get(aInput);
        }
    }
    
    /**
     * getState method: Get current UI state.
     */    
    UIState getState(){
        return uiState;
    }
}
