package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import static org.mockito.Mockito.*;

/**
 * Test class for the ParkingService class.
 * Contains tests for processing incoming and exiting vehicles and other related methods.
 */
@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    /**
     * Setup method to initialize the ParkingService before each test.
     * This method runs before each test to ensure a fresh setup for every test case.
     */
    @BeforeEach
    private void setUpPerTest() {
        try {          
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up ParkingService");
        }
    }

    /**
     * Test method for processing an exiting vehicle.
     * Verifies if the parking spot and ticket are updated correctly when a vehicle exits.
     */
    @Test
    public void processExitingVehicleTest() throws Exception {
    	
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
    	
        parkingService.processExitingVehicle();
        verify(ticketDAO, Mockito.times(1)).getNbTicket(any(String.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }
    
    /**
     * Test method for processing an incoming vehicle.
     * Verifies if the parking spot is marked as occupied and the ticket is saved.
     */
    @Test
    public void testProcessIncomingVehicle() throws Exception {
    	
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        
        parkingService = spy(new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO));
        
        doReturn(new ParkingSpot(1, ParkingType.CAR, true))
            .when(parkingService).getNextParkingNumberIfAvailable();
        
    	parkingService.processIncomingVehicle();
    	
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));
        verify(ticketDAO, times(1)).getNbTicket(any(String.class));
    }
    
    /**
     * Test method for processing an exiting vehicle where the update fails.
     * Verifies that no parking spot update occurs if the ticket update fails.
     */
    @Test
    public void processExitingVehicleTestUnableUpdate() throws Exception {
    	
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        
    	parkingService.processExitingVehicle();
        verify(ticketDAO, Mockito.times(1)).getNbTicket(any(String.class));
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
    }
    
    /**
     * Test method for retrieving the next available parking spot.
     * Verifies that the correct parking spot is returned when a parking slot is available.
     */
    @Test
    public void testGetNextParkingNumberIfAvailable() {
 
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    	
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

    	assertThat(parkingSpot).isNotNull();
    	assertThat(parkingSpot.getId()).isEqualTo(1);
        assertThat(parkingSpot.getParkingType()).isEqualTo(ParkingType.CAR);
    }
    
    /**
     * Test method for retrieving the next available parking spot when no parking spot is found.
     * Verifies that `null` is returned when no parking spot is available.
     */
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
    	
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);
    	
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
    	
    	assertNull(parkingSpot);
    }
    
    /**
     * Test method for handling invalid arguments in retrieving the next parking spot.
     * Verifies that an exception is caught and the method returns `null` when invalid arguments are provided.
     */
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
    	 ParkingService parkingServiceSpy = spy(parkingService);
                
         doThrow(new IllegalArgumentException("Entered input is invalid")).when(parkingServiceSpy).getVehichleType();
         
         ParkingSpot result = parkingServiceSpy.getNextParkingNumberIfAvailable();

         assertNull(result);
    }

}
