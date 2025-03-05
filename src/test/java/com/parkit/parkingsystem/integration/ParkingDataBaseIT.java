package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * Integration tests for parking system with database interactions.
 * Verifies the interactions between the application and the database.
 */
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static final Logger logger = LogManager.getLogger("ParkingDataBaseIT");

    @Mock
    private static InputReaderUtil inputReaderUtil;

    /**
     * Setup method to initialize database connections and DAOs before all tests.
     * This method is executed once before all tests in the class.
     */
    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    /**
     * Setup method for preparing the test environment before each test.
     * This method clears the database entries and sets up mock behaviors for user inputs.
     */
    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    /**
     * Test for parking a car.
     * Verifies that a ticket is created and the parking spot is marked as occupied.
     */
    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertThat(ticket).isNotNull();
        
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        assertThat(parkingSpot).isNotNull();
        assertThat(parkingSpot.isAvailable()).isFalse();
    }
    
    /**
     * Test for processing parking lot exit.
     * Verifies that after a vehicle exits, the ticket is updated with an exit time, 
     * and the parking spot is made available.
     */
    @Test
    public void testParkingLotExit(){
   
    	
        testParkingACar(); 
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        
        // Simulating an exit one hour after parking
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - 3600000));
        
        // Manually updating the ticket entry in the database to simulate a previous entry time
        Connection con = null;
        try {
        	con = dataBaseTestConfig.getConnection();
	        PreparedStatement ps = con.prepareStatement("UPDATE ticket SET IN_TIME=? WHERE VEHICLE_REG_NUMBER=?");
	        ps.setTimestamp(1, new Timestamp(ticket.getInTime().getTime()));
	        ps.setString(2, "ABCDEF");
	        ps.executeUpdate();
	        dataBaseTestConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error updating IN_TIME in database",ex);
        }finally {
        	dataBaseTestConfig.closeConnection(con);
        }

        parkingService.processExitingVehicle();
        
        ticket = ticketDAO.getTicket("ABCDEF");
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        
        assertThat(ticket).isNotNull();
        assertThat(ticket.getOutTime()).isNotNull();
        assertThat(ticket.getOutTime()).isAfter(ticket.getInTime());
        assertThat(ticket.getPrice()).isGreaterThan(0);
      
        // Retrieve the availability status of the parking spot from the database       boolean available = false;
        boolean available = false;
        try {
        	con = dataBaseTestConfig.getConnection();
	        PreparedStatement ps = con.prepareStatement(DBConstants.GET_PARKING_SPOT_ISAVAILABLE);
	        ps.setInt(1, parkingSpot.getId());
	        ResultSet rs = (ps.executeQuery());
	        if (rs.next()){
	        	available = rs.getBoolean(1);
	        }
	        dataBaseTestConfig.closeResultSet(rs);
	        dataBaseTestConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching ParkingSpot Availibility in database",ex); 
        }finally {
        	dataBaseTestConfig.closeConnection(con);
        }
        parkingSpot.setAvailable(available);

        assertThat(parkingSpot).isNotNull();
        assertTrue(parkingSpot.isAvailable());
    }
    
    /**
     * Test for parking lot exit with a recurring user.
     * Verifies that a discount is applied to the recurring user and the price is calculated correctly.
     */
    @Test
    public void testParkingLotExitRecurringUser(){
    	
    	testParkingLotExit();
    	testParkingLotExit();

    	double price = 0;
    	
    	// Retrieve the ticket price from the database
    	Connection con = null;
        try {
        	con = dataBaseTestConfig.getConnection();
	        PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET_PRICE);
	        ps.setString(1, "ABCDEF");
	        ResultSet rs = (ps.executeQuery());
	        if (rs.next()){
	        	price = Math.round( rs.getDouble(1) * 10.0) / 10.0;
	        }
	        dataBaseTestConfig.closeResultSet(rs);
	        dataBaseTestConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching Ticket Price in database",ex); 
        }finally {
        	dataBaseTestConfig.closeConnection(con);
        }
        
        double expected = Math.round(Fare.CAR_RATE_PER_HOUR * Fare.DISCOUNT_RATE * 10.0) / 10.0 ;
        
        assertThat(price).isEqualTo(expected);
    }

}
