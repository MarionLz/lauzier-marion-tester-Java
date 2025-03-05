package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Utility class for reading user input from the console.
 * It provides methods to read the selection input (as an integer) and vehicle registration number (as a string).
 */
public class InputReaderUtil {

    private static Scanner scan = new Scanner(System.in);
    private static final Logger logger = LogManager.getLogger("InputReaderUtil");

    /**
     * Reads the user selection input from the console.
     * This method reads the next line of input, tries to parse it as an integer, 
     * and returns the integer value. If the input is invalid, it logs the error 
     * and returns -1 to indicate a failure.
     * 
     * @return the integer value of the user input, or -1 if input is invalid
     */
    public int readSelection() {
        try {
            int input = Integer.parseInt(scan.nextLine());
            return input;
        }catch(Exception e){
            logger.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. Please enter valid number for proceeding further");
            return -1;
        }
    }

    /**
     * Reads the vehicle registration number from the console.
     * This method reads the next line of input, checks if it is a non-empty string,
     * and returns the registration number. If the input is invalid, it logs the error 
     * and throws an exception.
     * 
     * @return the vehicle registration number as a string
     * @throws Exception if the input is null, empty, or invalid
     */
    public String readVehicleRegistrationNumber() throws Exception {
        try {
            String vehicleRegNumber= scan.nextLine();
            if(vehicleRegNumber == null || vehicleRegNumber.trim().length()==0) {
                throw new IllegalArgumentException("Invalid input provided");
            }
            return vehicleRegNumber;
        }catch(Exception e){
            logger.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
            throw e;
        }
    }


}
