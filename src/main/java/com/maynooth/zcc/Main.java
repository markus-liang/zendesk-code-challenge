package com.maynooth.zcc;

import com.maynooth.zcc.ui.Controller;

/**
 * @author markus
 * Main class: the main class of this project.
 */
public class Main {
    /**
     * main method: the entry point of the application.
     */
    public static void main(String[] args){
        try
        {
            Controller controller = new Controller();
            controller.start();
        }catch(Exception e){
            System.out.println("Oops.. something went wrong. Please contact the administrator.");
        }
    }
}
