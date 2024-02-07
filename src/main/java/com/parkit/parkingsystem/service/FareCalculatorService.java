package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;

public class FareCalculatorService {

    /**
     * Calculates the fare for a parking ticket based on the duration and parking type.
     *
     * @param ticket   The parking ticket for which to calculate the fare.
     * @param discount Indicates whether a discount should be applied.
     * @throws IllegalArgumentException If the provided exit time is incorrect or earlier than the entry time.
     */
    public void calculateFare(Ticket ticket, boolean discount) throws IllegalArgumentException {
        // Checks if the exit time is correct
        if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Incorrect exit time: " + ticket.getOutTime().toString());
        }

        // Gets entry and exit times in milliseconds
        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        // Calculates duration in hours
        double duration = (double) (outHour - inHour) / (60 * 60 * 1000);

        // Selects the parking type
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                // Calculates fare for cars
                if (duration <= 0.5) {
                    ticket.setPrice(0);
                    break;
                }
                if (discount) {
                    double totalPrice = (duration * Fare.CAR_RATE_PER_HOUR - (duration * Fare.CAR_RATE_PER_HOUR * 0.05));
                    BigDecimal roundedPrice = BigDecimal.valueOf(totalPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                    double roundedPriceDouble = roundedPrice.doubleValue();
                    ticket.setPrice(roundedPriceDouble);
                    break;
                }
                double totalPrice = duration * Fare.CAR_RATE_PER_HOUR;
                BigDecimal roundedPrice = BigDecimal.valueOf(totalPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                double roundedPriceDouble = roundedPrice.doubleValue();
                ticket.setPrice(roundedPriceDouble);
                break;
            }
            case BIKE: {
                // Calculates fare for bikes
                if (duration <= 0.5) {
                    ticket.setPrice(0);
                    break;
                }
                if (discount) {
                    double totalPrice = (duration * Fare.BIKE_RATE_PER_HOUR) - (duration * Fare.BIKE_RATE_PER_HOUR * 0.05);
                    BigDecimal roundedPrice = BigDecimal.valueOf(totalPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                    double roundedPriceDouble = roundedPrice.doubleValue();
                    ticket.setPrice(roundedPriceDouble);
                    break;
                }
                double totalPrice = (duration * Fare.BIKE_RATE_PER_HOUR);
                BigDecimal roundedPrice = BigDecimal.valueOf(totalPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
                double roundedPriceDouble = roundedPrice.doubleValue();
                ticket.setPrice(roundedPriceDouble);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown parking type");
        }
    }

    /**
     * Calculates the fare for a parking ticket without a discount.
     *
     * @param ticket The parking ticket for which to calculate the fare.
     */
    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, false);
    }
}