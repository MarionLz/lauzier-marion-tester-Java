package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * Unit tests for the FareCalculatorService class.
 * These tests validate the fare calculation logic based on different parking durations, vehicle types, and discount scenarios.
 */
public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    /**
     * Test case to validate fare calculation for a car with exactly 1 hour of parking.
     * It ensures that the fare is calculated based on the car's rate.
     */
    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    /**
     * Test case to validate fare calculation for a bike with exactly 1 hour of parking.
     * It ensures that the fare is calculated based on the bike's rate.
     */
    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    /**
     * Test case to validate fare calculation for an unknown parking type.
     * It ensures that a NullPointerException is thrown for an unknown parking type.
     */
    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Test case to validate fare calculation for a bike with a future in-time.
     * It ensures that an IllegalArgumentException is thrown when the in-time is in the future.
     */
    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Test case to validate fare calculation for a bike with less than 1 hour of parking time.
     * It ensures that the fare is calculated correctly for durations less than 1 hour.
     */
    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    /**
     * Test case to validate fare calculation for a car with less than 1 hour of parking time.
     * It ensures that the fare is calculated correctly for durations less than 1 hour.
     */
    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    /**
     * Test case to validate fare calculation for a car with more than 1 day of parking time.
     * It ensures that the fare is calculated correctly for durations longer than 1 day.
     */
    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    /**
     * Test case to validate that parking for less than 30 minutes is free for cars.
     * It ensures that parking for less than 30 minutes results in a fare of 0.
     */
    @Test
    public void calculateFareCarWithLessThan30minutesParkingTimeDescription() {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (15 * 60 * 1000) );//15 minutes parking time should give free parking
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }
    
    /**
     * Test case to validate that parking for less than 30 minutes is free for bikes.
     * It ensures that parking for less than 30 minutes results in a fare of 0.
     */
    @Test
    public void calculateFareBikeWithLessThan30minutesParkingTimeDescription() {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (15 * 60 * 1000) );//15 minutes parking time should give free parking
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }
    
    /**
     * Test case to validate fare calculation for a car with a discount.
     * It ensures that the discount is correctly applied to the fare for cars.
     */
    @Test
    public void calculateFareCarWithDiscountDescription() {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (2 * 60 * 60 * 1000) );//2 hour parking time should give 2 * parking fare per hour * O,95
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setDiscount(true);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((2 * Fare.CAR_RATE_PER_HOUR * Fare.DISCOUNT_RATE), ticket.getPrice());
    }
    
    /**
     * Test case to validate fare calculation for a bike with a discount.
     * It ensures that the discount is correctly applied to the fare for bikes.
     */
    @Test
    public void calculateFareBikeWithDiscountDescription() {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (2 * 60 * 60 * 1000) );//2 hour parking time should give 2 * parking fare per hour * O,95
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setDiscount(true);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((2 * Fare.BIKE_RATE_PER_HOUR * Fare.DISCOUNT_RATE), ticket.getPrice());
    }
    
    

}
