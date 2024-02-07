package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){
        dataBasePrepareService.clearDataBaseEntries();
    }

    /**
     * Test case for parking a car using the ParkingService.
     */
    @Test
    public void testParkingACar() {
        // Create a ParkingService instance with mock objects
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Simulate the process of a car entering the parking lot
        parkingService.processIncomingVehicle();

        // TODO: Check that a ticket is actually saved in the database and the Parking table is updated with availability

        // Retrieve the ticket from the database using the vehicle registration number
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        // Assertions to verify the correctness of the parking process
        assertNotNull(ticket.getInTime());
        assertEquals(1, ticket.getParkingSpot().getId());
        assertEquals(ParkingType.CAR, ticket.getParkingSpot().getParkingType());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertFalse(ticket.getParkingSpot().isAvailable());

        // Check the next available slot for cars in the ParkingSpotDAO
        int nextAvailableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(2, nextAvailableSlot);
    }

    /**
     * Test case for parking a bike using the ParkingService.
     */
    @Test
    public void testParkingABike() {
        // Mock user input to simulate selecting bike as the vehicle type
        when(inputReaderUtil.readSelection()).thenReturn(2);

        // Create a ParkingService instance with mock objects
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Simulate the process of a bike entering the parking lot
        parkingService.processIncomingVehicle();

        // Retrieve the ticket from the database using the vehicle registration number
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        // Set inTime and parkingSpot for the ticket (simulating a saved ticket with a parking spot)
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(new ParkingSpot(4, ParkingType.BIKE, false));
        ticketDAO.saveTicket(ticket);

        // Assertions to verify the correctness of the parking process
        assertNotNull(ticket.getInTime());
        assertEquals(4, ticket.getParkingSpot().getId());
        assertEquals(ParkingType.BIKE, ticket.getParkingSpot().getParkingType());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertFalse(ticket.getParkingSpot().isAvailable());
    }

    /**
     * Test case for the parking lot entrance process with an error scenario using the ParkingService.
     * This test simulates a scenario where an error occurs during the parking lot entrance process.
     */
    @Test
    public void testParkingEnterWhitError() {
        // Create a new ParkingService instance for the entrance process
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Simulate a vehicle entering the parking lot
        parkingService.processIncomingVehicle();

        // Retrieve the ticket from the database using the vehicle registration number
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        // Set inTime to a future time and create a ParkingSpot with a negative ID
        ticket.setInTime(new Date(System.currentTimeMillis() + (60 * 60 * 1000)));
        ticket.setParkingSpot(new ParkingSpot(-1, ParkingType.CAR, true));
        ticketDAO.saveTicket(ticket);

        // Assertions to check the ticket details and the ParkingSpot availability
        assertEquals(-1, ticket.getParkingSpot().getId());
        assertEquals(ParkingType.CAR, ticket.getParkingSpot().getParkingType());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertTrue(ticket.getParkingSpot().isAvailable());
    }

    /**
     * Test case for the parking lot exit process using the ParkingService.
     * This test builds on the "testParkingACar" test to simulate a car entering the parking lot.
     */
    @Test
    public void testParkingLotExit() {
        // Call the testParkingACar method to simulate a car entering the parking lot
        testParkingACar();

        // Create a new ParkingService instance for the exit process
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Retrieve the ticket from the database using the vehicle registration number
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        // Set inTime, parkingSpot, and update ticket to simulate a saved ticket with a parking spot
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
        ticketDAO.updateTicket(ticket);
        ticketDAO.saveTicket(ticket);

        // Simulate the process of a vehicle exiting the parking lot
        parkingService.processExitingVehicle();

        // Assertions to check that the fare, out time, and price are populated correctly in the database
        ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getInTime());
        assertNotNull(ticket.getOutTime());
        assertNotNull(ticket.getPrice());
    }

    /**
     * Test case for the parking lot exit process with a recurring user using the ParkingService.
     * This test builds on the "testParkingACar" test to simulate a car entering the parking lot.
     */
    @Test
    public void testParkingLotExitRecurringUser() {
        // Call the testParkingACar method to simulate a car entering the parking lot
        testParkingACar();

        // Create a new ParkingService instance for the exit process
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Simulate a recurring user by processing an incoming vehicle
        parkingService.processIncomingVehicle();

        // Retrieve the ticket from the database using the vehicle registration number
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        // Set inTime, outTime, and update ticket to simulate a saved ticket with in and out times
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setOutTime(new Date(System.currentTimeMillis()));
        ticketDAO.updateTicket(ticket);
        ticketDAO.saveTicket(ticket);

        // Simulate the process of a recurring user vehicle exiting the parking lot
        parkingService.processExitingVehicle();

        // Calculate the expected total price with a 5% discount for a recurring user
        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();
        double duration = (double) (outHour - inHour) / (60 * 60 * 1000);
        double totalPrice = (duration * Fare.CAR_RATE_PER_HOUR - (duration * Fare.CAR_RATE_PER_HOUR * 0.05));
        BigDecimal roundedPrice = BigDecimal.valueOf(totalPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
        double roundedPriceDouble = roundedPrice.doubleValue();

        // Assertions to check that the ticket is marked as a recurring user and the calculated price matches
        assertEquals(true, ticketDAO.getNbTicket("ABCDEF"));
        assertEquals(roundedPriceDouble, ticketDAO.getTicket("ABCDEF").getPrice());
    }

    /**
     * Test case for the parking lot exit process when the ticket does not have a valid inTime.
     * This test simulates a scenario where the inTime of the ticket is null during the exit process.
     */
    @Test
    public void testParkingExitNoInTime() {
        // Perform the parking lot entrance process to generate a valid ticket
        testParkingACar();

        // Create a new ParkingService instance for the exit process
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Simulate a vehicle exiting the parking lot
        parkingService.processExitingVehicle();

        // Retrieve the ticket from the database using the vehicle registration number
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        // Set the inTime of the ticket to null
        ticket.setInTime(null);
        ticketDAO.saveTicket(ticket);

        // Assertion to check that the inTime of the ticket is null
        assertNull(ticket.getInTime());
    }

    /**
     * Test case for the parking lot exit process when the ticket does not have a valid vehicle registration number.
     * This test simulates a scenario where the vehicle registration number of the ticket is set to null during the exit process.
     */
    @Test
    public void testParkingExitNoRegNumber() {
        // Perform the parking lot entrance process to generate a valid ticket
        testParkingACar();

        // Create a new ParkingService instance for the exit process
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Simulate a vehicle exiting the parking lot
        parkingService.processExitingVehicle();

        // Retrieve the ticket from the database using the valid vehicle registration number
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        // Set the vehicle registration number of the ticket to null
        ticket.setVehicleRegNumber(null);
        ticketDAO.saveTicket(ticket);

        // Assertion to check that attempting to retrieve a ticket with a null registration number returns null
        assertNull(ticketDAO.getTicket(null));
    }
}
