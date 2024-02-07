package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static TicketDAO ticketDAO;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
    }

    /**
     * Test case for saving a ticket in the DAO.
     * This test checks if the DAO returns false when attempting to save a ticket with a parking spot that is not available.
     */
    @Test
    void saveTicketInDAO() {
        // Create a ParkingSpot instance with specific details for testing (not available)
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Create a Ticket instance with specific details for testing
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setOutTime(new Date());

        // Call the DAO method to save the ticket and get the return status
        boolean saveTicketReturn = ticketDAO.saveTicket(ticket);

        // Assertion to check if the return status is false, indicating the parking spot is not available
        assertFalse(saveTicketReturn);
    }

    /**
     * Test case for retrieving a ticket from the DAO by vehicle registration number.
     * This test checks if the DAO successfully retrieves a saved ticket by its vehicle registration number.
     */
    @Test
    void getTicketDAO() {
        // Create a ParkingSpot instance with specific details for testing
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Create a Ticket instance with specific details for testing
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setOutTime(new Date());

        // Save the ticket using the DAO
        ticketDAO.saveTicket(ticket);

        // Retrieve the ticket by its vehicle registration number
        ticket = ticketDAO.getTicket("ABCDEF");

        // Assertion to check if the retrieved ticket is not null
        assertNotNull(ticket);
    }

    /**
     * Test case for updating a ticket in the DAO.
     * This test checks if the DAO successfully updates an existing ticket with new information.
     */
    @Test
    void updateTicketDAO() {
        // Create a ParkingSpot instance with specific details for testing
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Create an initial Ticket instance with specific details for testing
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000)));
        ticket.setOutTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));

        // Save the initial ticket using the DAO
        ticketDAO.saveTicket(ticket);

        // Create a new Ticket instance with updated information
        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(1);
        updatedTicket.setParkingSpot(parkingSpot);
        updatedTicket.setVehicleRegNumber("ABCDEF");
        updatedTicket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        updatedTicket.setOutTime(new Date());

        // Attempt to update the existing ticket using the DAO
        boolean updateTicketReturn = ticketDAO.updateTicket(updatedTicket);

        // Assertion to check if the ticket update was successful
        assertTrue(updateTicketReturn);
    }

    /**
     * Test case for checking the number of tickets for a recurrent user in the DAO.
     * This test verifies if the DAO correctly identifies a recurrent user based on the number of tickets.
     */
    @Test
    void getNbTicketDAO() {
        // Create a ParkingSpot instance with specific details for testing
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        // Create three Ticket instances with different inTime values for testing
        Ticket ticket1 = new Ticket();
        ticket1.setId(1);
        ticket1.setParkingSpot(parkingSpot);
        ticket1.setVehicleRegNumber("ABCDEF");
        ticket1.setInTime(new Date(System.currentTimeMillis() - (3 * 60 * 60 * 1000)));
        ticket1.setOutTime(new Date());

        Ticket ticket2 = new Ticket();
        ticket2.setId(2);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setVehicleRegNumber("ABCDEF");
        ticket2.setInTime(new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000)));
        ticket2.setOutTime(new Date());

        Ticket ticket3 = new Ticket();
        ticket3.setId(3);
        ticket3.setParkingSpot(parkingSpot);
        ticket3.setVehicleRegNumber("ABCDEF");
        ticket3.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket3.setOutTime(new Date());

        // Save the three tickets using the DAO
        ticketDAO.saveTicket(ticket1);
        ticketDAO.saveTicket(ticket2);
        ticketDAO.saveTicket(ticket3);

        // Check if the DAO correctly identifies the user as recurrent
        boolean recurrentUser = ticketDAO.getNbTicket("ABCDEF");

        // Assertion to verify if the user is recognized as recurrent
        assertTrue(recurrentUser);
    }
}