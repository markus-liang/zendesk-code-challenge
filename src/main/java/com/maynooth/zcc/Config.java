package com.maynooth.zcc;
import java.io.File;
import java.nio.file.Files;
import java.util.*;

/**
 * @author markus
 * Config class: a singleton class used to handle the configuration file
 */
public class Config {
    private static Config instance;
    private static Hashtable<String, String> settings;
    
    /**
     * Config constructor read the configuration file 
     * and put the value into a HashTable
     */    
    private Config(){
        settings = new Hashtable<>();
        // default settings
        settings.put("API","");
        settings.put("USERNAME","");
        settings.put("PASSWORD","");
        
        try
        {
            String path = Config.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            
            // released path            
            File f = new File(path.substring(0, path.lastIndexOf("/")) + "/config");
            if (!f.exists()) {
                // debug path            
                path = path.substring(0, path.lastIndexOf("/"));
                path = path.substring(0, path.lastIndexOf("/"));
                f = new File(path + "/config");
            }
            
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
               String line = scanner.nextLine();
               String[] lines = line.split("=");
               switch(lines[0].trim()){
                   case "API": settings.replace("API", lines[1].trim());
                        break;
                   case "USERNAME": settings.replace("USERNAME", lines[1].trim());
                        break;
                   case "PASSWORD": settings.replace("PASSWORD", lines[1].trim());
                        break;
               }
            }
        }catch(Exception ex){
            System.out.println("Error reading config file : " + ex.getMessage());
        }
    }
    
    /**
     * getInstance method: return the Config object
     */    
    public static Config getInstance(){
        if(instance == null){
            instance = new Config();
        }
        return instance;
    }
    
    /**
     * get method: get the setting value of the given key
     * @param aKey: the configuration key
     */    
    public String get(String aKey){
        return settings.get(aKey);
    }
}
