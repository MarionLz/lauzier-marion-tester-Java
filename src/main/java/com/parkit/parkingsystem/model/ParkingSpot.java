package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * Model class representing a parking spot.
 */
public class ParkingSpot {
    private int number;
    private ParkingType parkingType;
    private boolean isAvailable;

    /**
     * Constructor to initialize a parking spot.
     *
     * @param number      The unique identifier of the parking spot.
     * @param parkingType The type of vehicle the parking spot is for.
     * @param isAvailable The availability status of the parking spot.
     */
    public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
        this.number = number;
        this.parkingType = parkingType;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return number;
    }
    
    public void setId(int number) {
        this.number = number;
    }
    
    public ParkingType getParkingType() {
        return parkingType;
    }


    public void setParkingType(ParkingType parkingType) {
        this.parkingType = parkingType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    /**
     * Checks if two parking spots are equal based on their unique number.
     *
     * @param o The object to compare.
     * @return {@code true} if they are the same parking spot, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSpot that = (ParkingSpot) o;
        return number == that.number;
    }

    /**
     * Generates the hash code for the parking spot based on its unique number.
     *
     * @return The hash code of the parking spot.
     */
    @Override
    public int hashCode() {
        return number;
    }
}
