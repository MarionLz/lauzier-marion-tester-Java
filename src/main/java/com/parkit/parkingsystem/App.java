package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main application class for the Parking System.
 * This class initializes and starts the interactive shell interface.
 */
public class App {
	
    private static final Logger logger = LogManager.getLogger("App");
    
    public static void main(String args[]){
        logger.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
