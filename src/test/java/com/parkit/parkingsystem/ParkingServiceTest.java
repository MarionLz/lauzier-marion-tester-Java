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

import java.lang.System.Logger;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            //when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            //ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            //Ticket ticket = new Ticket();
            //ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            //ticket.setParkingSpot(parkingSpot);
            //ticket.setVehicleRegNumber("ABCDEF");

            //when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            
            //when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

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
    
    @Test
    public void testProcessIncomingVehicle() throws Exception {
    	
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        parkingService = spy(new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO));
        
        // Mock de getNextParkingNumberIfAvailable()
        doReturn(new ParkingSpot(1, ParkingType.CAR, true))
            .when(parkingService).getNextParkingNumberIfAvailable();
        
        //when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

    	parkingService.processIncomingVehicle();
        // Assert: Vérifier que la place de parking a été marquée comme occupée
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));

        // Vérifier que le ticket a bien été sauvegardé
        verify(ticketDAO, times(1)).saveTicket(any(Ticket.class));

        // Vérifier que la méthode getNbTicket a été appelée pour vérifier si c'est un client régulier
        verify(ticketDAO, times(1)).getNbTicket(any(String.class));
    }
    
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
    
    @Test
    public void testGetNextParkingNumberIfAvailable() {
 
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
    	
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

    	assertThat(parkingSpot).isNotNull();
    	assertThat(parkingSpot.getId()).isEqualTo(1);
        assertThat(parkingSpot.getParkingType()).isEqualTo(ParkingType.CAR);
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
    	
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);
    	
    	ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
    	
    	assertNull(parkingSpot);
    }
    
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
    	 ParkingService parkingServiceSpy = spy(parkingService);
                
         // Simuler une méthode getVehichleType() qui lève une IllegalArgumentException
         doThrow(new IllegalArgumentException("Entered input is invalid")).when(parkingServiceSpy).getVehichleType();
         
         // Act
         ParkingSpot result = parkingServiceSpy.getNextParkingNumberIfAvailable();

         // Assert
         assertNull(result); // On vérifie que le résultat est bien null lorsque l'exception est levée
         //verify(logger).error(eq("Error parsing user input for type of vehicle"), any(IllegalArgumentException.class)); // Vérification du log
    }

}
