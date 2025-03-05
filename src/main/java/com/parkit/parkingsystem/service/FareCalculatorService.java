package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * Service responsible for calculating the fare for a parking ticket based on the parking duration
 * and parking type (CAR or BIKE), including the application of any discounts.
 */
public class FareCalculatorService {

	/**
     * Calculates the parking fare for the given ticket based on the parking duration and type.
     * The fare is updated in the ticket object.
     * 
     * @param ticket the ticket for which the fare needs to be calculated
     * @throws IllegalArgumentException if the outTime is null or before inTime, or if the parking type is unknown
     */
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        float duration = (float)(outHour - inHour) / 3600000;
        
        if(duration < 0.5) {
        	ticket.setPrice(0);
        }
        else {
            switch (ticket.getParkingSpot().getParkingType()){
	            case CAR: {
	                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
	                break;
	            }
	            case BIKE: {
	                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
	                break;
	            }
	            default: throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
        if (ticket.getDiscount()) {
        	ticket.setPrice(ticket.getPrice() * Fare.DISCOUNT_RATE);
        }

    }
}