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

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability

        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticket.getInTime());
        assertEquals(1, ticket.getParkingSpot().getId());
        assertEquals(ParkingType.CAR, ticket.getParkingSpot().getParkingType());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertFalse(ticket.getParkingSpot().isAvailable());
        int nextAvaibleSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertEquals(2, nextAvaibleSlot);
    }

    @Test
    public void testParkingABike(){
        when(inputReaderUtil.readSelection()).thenReturn(2);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(new ParkingSpot(4, ParkingType.BIKE, false));
        ticketDAO.saveTicket(ticket);

        assertNotNull(ticket.getInTime());
        assertEquals(4, ticket.getParkingSpot().getId());
        assertEquals(ParkingType.BIKE, ticket.getParkingSpot().getParkingType());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertFalse(ticket.getParkingSpot().isAvailable());
    }

    @Test
    public void testParkingLotExit() {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
        ticketDAO.updateTicket(ticket);
        ticketDAO.saveTicket(ticket);

        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database

        ticket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(ticket.getInTime());
        assertNotNull(ticket.getOutTime());
        assertNotNull(ticket.getPrice());
    }

    // Test d’intégration pour la fonctionnalité de remise de 5% : testParkingLotExitRecurringUser.
    // Il doit tester le calcul du prix d’un ticket via l’appel de processIncomingVehicle et processExitingVehicle dans
    // le cas d’un utilisateur récurrent
    @Test
    public void testParkingLotExitRecurringUser() {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setOutTime(new Date(System.currentTimeMillis()));
        ticketDAO.updateTicket(ticket);
        ticketDAO.saveTicket(ticket);

        parkingService.processExitingVehicle();

        assertEquals(true, ticketDAO.getNbTicket("ABCDEF"));
        // arrondi à 2 décimales
        double reducePrice = Math.round((0.95 * Fare.CAR_RATE_PER_HOUR) * 100.6) / 100.0;
        assertEquals(reducePrice, ticketDAO.getTicket("ABCDEF").getPrice());
    }

    @Test
    public void testParkingEnterWhitError(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() + (60 * 60 * 1000)));
        ticket.setParkingSpot(new ParkingSpot(-1, ParkingType.CAR, true));
        ticketDAO.saveTicket(ticket);

        assertEquals(-1, ticket.getParkingSpot().getId());
        assertEquals(ParkingType.CAR, ticket.getParkingSpot().getParkingType());
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertTrue(ticket.getParkingSpot().isAvailable());
    }

    @Test
    public void testParkingExitNoInTime(){
        testParkingACar();

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setInTime(null);
        ticketDAO.saveTicket(ticket);

        assertNull(ticket.getInTime());
    }

    @Test
    public void testParkingExitNoRegNumber(){
        testParkingACar();

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        ticket.setVehicleRegNumber(null);
        ticketDAO.saveTicket(ticket);

        assertNull(ticketDAO.getTicket(null));
    }
}
