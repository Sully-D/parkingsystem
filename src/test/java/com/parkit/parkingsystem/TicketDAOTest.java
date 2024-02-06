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

    @Test
    void saveTicketInDAO() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setOutTime(new Date());

        boolean saveTicketReturn = ticketDAO.saveTicket(ticket);

        assertFalse(saveTicketReturn);
    }

    @Test
    void getTicketDAO() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setOutTime(new Date());
        ticketDAO.saveTicket(ticket);

        ticket = ticketDAO.getTicket("ABCDEF");

        assertNotNull(ticket);
    }

    @Test
    void updateTicketDAO() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000)));
        ticket.setOutTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticketDAO.saveTicket(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setId(1);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setVehicleRegNumber("ABCDEF");
        ticket2.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket2.setOutTime(new Date());

        boolean updateTicketReturn = ticketDAO.updateTicket(ticket2);

        assertTrue(updateTicketReturn);
    }

    @Test
    void getNbTicketDAO() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setInTime(new Date(System.currentTimeMillis() - (3 * 60 * 60 * 1000)));
        ticket.setOutTime(new Date());
        ticketDAO.saveTicket(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setId(1);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setVehicleRegNumber("ABCDEF");
        ticket2.setInTime(new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000)));
        ticket2.setOutTime(new Date());
        ticketDAO.saveTicket(ticket2);

        Ticket ticket3 = new Ticket();
        ticket2.setId(1);
        ticket2.setParkingSpot(parkingSpot);
        ticket2.setVehicleRegNumber("ABCDEF");
        ticket2.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket2.setOutTime(new Date());
        ticketDAO.saveTicket(ticket2);

        boolean recurentUser = ticketDAO.getNbTicket("ABCDEF");
        assertTrue(recurentUser);
    }
}