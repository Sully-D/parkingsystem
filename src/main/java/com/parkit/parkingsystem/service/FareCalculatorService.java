package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = (double)(outHour - inHour) / (60 * 60 * 1000); // Convert millis to hours

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                if (duration <= 0.5){
                    ticket.setPrice(0 * Fare.CAR_RATE_PER_HOUR);
                    // arondissement à 2 chiffres après la virgule
                    ticket.setPrice((double) Math.round(ticket.getPrice() * 100) / 100);
                    break;
                }
                if (discount){
                    ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR - (duration * Fare.CAR_RATE_PER_HOUR * 0.05)));
                    // arondissement à 2 chiffres après la virgule
                    ticket.setPrice((double) Math.round(ticket.getPrice() * 100) / 100);
                    break;
                }
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                // arondissement à 2 chiffres après la virgule
                ticket.setPrice((double) Math.round(ticket.getPrice() * 100) / 100);
                break;
            }
            case BIKE: {
                if (duration <= 0.5){
                    ticket.setPrice(0 * Fare.CAR_RATE_PER_HOUR);
                    // arondissement à 2 chiffres après la virgule
                    ticket.setPrice((double) Math.round(ticket.getPrice() * 100) / 100);
                    break;
                }
                if (discount){
                    ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR) - (duration * Fare.BIKE_RATE_PER_HOUR * 0.05));
                    // arondissement à 2 chiffres après la virgule
                    ticket.setPrice((double) Math.round(ticket.getPrice() * 100) / 100);
                    break;
                }
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                // arondissement à 2 chiffres après la virgule
                ticket.setPrice((double) Math.round(ticket.getPrice() * 100) / 100);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    public void calculateFare(Ticket ticket){
        calculateFare(ticket, false);
    }
}