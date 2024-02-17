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

import java.math.BigDecimal;
import java.util.Date;

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
     * Test case for calculating the fare for a car parked in the parking lot.
     * This test simulates a scenario where a car has been parked, and the fare needs to be calculated.
     */
    @Test
    public void calculateFareCar() {
        // Set up the test scenario with a car parked for one hour
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calculate the fare for the scenario
        fareCalculatorService.calculateFare(ticket);

        // Assertion to check that the calculated fare matches the expected fare for a car
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    /**
     * Test case for calculating the fare for a bike parked in the parking lot.
     * This test simulates a scenario where a bike has been parked, and the fare needs to be calculated.
     */
    @Test
    public void calculateFareBike() {
        // Set up the test scenario with a bike parked for one hour
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calculate the fare for the scenario
        fareCalculatorService.calculateFare(ticket);

        // Assertion to check that the calculated fare matches the expected fare for a bike
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    /**
     * Test case for calculating the fare with an unknown parking spot type.
     * This test simulates a scenario where the parking spot type is unknown, resulting in a NullPointerException.
     */
    @Test
    public void calculateFareUnkownType() {
        // Set up the test scenario with an unknown parking spot type
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Assertion to check that calculating the fare with an unknown parking spot type results in a NullPointerException
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Test case for calculating the fare for a bike with a future in-time.
     * This test simulates a scenario where the in-time provided is in the future, resulting in an IllegalArgumentException.
     */
    @Test
    public void calculateFareBikeWithFutureInTime() {
        // Set up the test scenario with a future in-time for a bike
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Assertion to check that calculating the fare with a future in-time for a bike results in an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Test case for calculating the fare for a bike with less than one hour of parking time.
     * This test simulates a scenario where the bike has been parked for less than an hour, resulting in a discounted fare.
     */
    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        // Set up the test scenario with less than one hour of parking time for a bike
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calculate the fare for a bike with less than one hour of parking time
        fareCalculatorService.calculateFare(ticket);

        // Rounding to 2 decimal places
        double price = (0.75 * Fare.BIKE_RATE_PER_HOUR) * 100 / 100;
        BigDecimal roundedPrice = BigDecimal.valueOf(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        double roundedPriceDouble = roundedPrice.doubleValue();

        // Assertion to check that the calculated fare matches the expected discounted fare
        assertEquals(roundedPriceDouble, ticket.getPrice(), 0.01);
    }

    /**
     * Test case for calculating the fare for a car with less than one hour of parking time.
     * This test simulates a scenario where the car has been parked for less than an hour, resulting in a discounted fare.
     */
    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        // Set up the test scenario with less than one hour of parking time for a car
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calculate the fare for a car with less than one hour of parking time
        fareCalculatorService.calculateFare(ticket);

        // Rounding to 2 decimal places
        double price = (0.75 * Fare.CAR_RATE_PER_HOUR) * 100 / 100;
        BigDecimal roundedPrice = BigDecimal.valueOf(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        double roundedPriceDouble = roundedPrice.doubleValue();

        // Assertion to check that the calculated fare matches the expected discounted fare
        assertEquals(roundedPriceDouble, ticket.getPrice(), 0.01);
    }

    /**
     * Test case for calculating the fare for a car with more than a day of parking time.
     * This test simulates a scenario where the car has been parked for more than a day, resulting in the maximum daily fare.
     */
    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        // Set up the test scenario with more than a day of parking time for a car
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000)); // 24 hours parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calculate the fare for a car with more than a day of parking time
        fareCalculatorService.calculateFare(ticket);

        // Rounding to 2 decimal places
        double price = (24 * Fare.CAR_RATE_PER_HOUR) * 100 / 100;
        BigDecimal roundedPrice = BigDecimal.valueOf(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        double roundedPriceDouble = roundedPrice.doubleValue();

        // Assertion to check that the calculated fare matches the expected maximum daily fare
        assertEquals(roundedPriceDouble, ticket.getPrice(), 0.01);
    }

    /**
     * Test case for calculating the fare for a car with less than 30 minutes of parking time.
     * This test simulates a scenario where the car has been parked for less than 30 minutes, resulting in free parking.
     */
    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime() {
        // Set up the test scenario with less than 30 minutes of parking time for a car
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000)); // 30 minutes of parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calculate the fare for a car with less than 30 minutes of parking time
        fareCalculatorService.calculateFare(ticket);

        // Rounding to 2 decimal places
        double price = (0 * Fare.CAR_RATE_PER_HOUR) * 100 / 100;
        BigDecimal roundedPrice = BigDecimal.valueOf(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        double roundedPriceDouble = roundedPrice.doubleValue();

        // Assertion to check that the calculated fare matches the expected free parking fare
        assertEquals(roundedPriceDouble, ticket.getPrice(), 0.01);
    }

    /**
     * Test case for calculating the fare for a bike with less than 30 minutes of parking time.
     * This test simulates a scenario where the bike has been parked for less than 30 minutes, resulting in free parking.
     */
    @Test
    public void calculateFareBikeWithLessThan30minutesParkingTime() {
        // Set up the test scenario with less than 30 minutes of parking time for a bike
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000)); // 30 minutes of parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calculate the fare for a bike with less than 30 minutes of parking time
        fareCalculatorService.calculateFare(ticket);

        // Rounding to 2 decimal places
        double price = (0 * Fare.BIKE_RATE_PER_HOUR) * 100 / 100;
        BigDecimal roundedPrice = BigDecimal.valueOf(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        double roundedPriceDouble = roundedPrice.doubleValue();

        // Assertion to check that the calculated fare matches the expected free parking fare
        assertEquals(roundedPriceDouble, ticket.getPrice(), 0.01);
    }

    /**
     * Test case for calculating the fare for a car with a discount.
     * This test simulates a scenario where the car has been parked for 45 minutes, and a discount is applied to the fare.
     */
    @Test
    public void calculateFareCarWithDiscount() {
        // Set up the test scenario with 45 minutes of parking time for a car and a discount applied
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes of parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calculate the fare for a car with a discount
        fareCalculatorService.calculateFare(ticket, true);

        // Rounding to 2 decimal places
        double price = (0.75 * Fare.CAR_RATE_PER_HOUR - 0.75 * Fare.CAR_RATE_PER_HOUR * 0.05) * 100 / 100;
        BigDecimal roundedPrice = BigDecimal.valueOf(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        double roundedPriceDouble = roundedPrice.doubleValue();

        // Assertion to check that the calculated fare matches the expected discounted fare
        assertEquals(roundedPriceDouble, ticket.getPrice(), 0.1);
    }

    /**
     * Test case for calculating the fare for a bike with a discount.
     * This test simulates a scenario where the bike has been parked for 45 minutes, and a discount is applied to the fare.
     */
    @Test
    public void calculateFareBikeWithDiscount() {
        // Set up the test scenario with 45 minutes of parking time for a bike and a discount applied
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000)); // 45 minutes of parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calculate the fare for a bike with a discount
        fareCalculatorService.calculateFare(ticket, true);

        // Rounding to 2 decimal places
        double price = (0.75 * Fare.BIKE_RATE_PER_HOUR - 0.75 * Fare.BIKE_RATE_PER_HOUR * 0.05) * 100 / 100;
        BigDecimal roundedPrice = BigDecimal.valueOf(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        double roundedPriceDouble = roundedPrice.doubleValue();

        // Assertion to check that the calculated fare matches the expected discounted fare
        assertEquals(roundedPriceDouble, ticket.getPrice(), 0.1);
    }

    /**
     * Test case for calculating the fare with an illegal argument for parking type.
     * This test simulates a scenario where an illegal parking type is provided, resulting in an IllegalArgumentException.
     */
    @Test
    public void calculateFareIllegalArgumentParkingType() {
        // Set up the test scenario with valid parking time and an illegal parking type
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000)); // 1 hour of parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.TEST, false);

        // Set the ticket details for the scenario
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Assertion to check that an IllegalArgumentException is thrown for an illegal parking type
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }
}
