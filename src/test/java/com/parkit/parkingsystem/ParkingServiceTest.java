package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;


    @BeforeEach
    private void setUpPerTest() {
        try {
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }


    /**
     * Teste le processus d'entrée d'un véhicule dans le parking.
     *
     * @throws Exception si une exception est levée pendant le test.
     */
    @Test
    public void testProcessIncomingVehicle() throws Exception {
        // Configuration des comportements simulés pour les objets mockés
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        // Appel de la méthode à tester
        parkingService.processIncomingVehicle();

        // Vérification des interactions avec les objets mockés
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    /**
     * Teste le processus d'entrée d'un véhicule de type moto dans le parking.
     *
     * @throws Exception si une exception est levée pendant le test.
     */
    @Test
    public void testProcessIncomingVehicleWithBike() throws Exception {
        // Configuration des comportements simulés pour les objets mockés
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(2);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        // Appel de la méthode à tester
        parkingService.processIncomingVehicle();

        // Vérification des interactions avec les objets mockés
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.BIKE);
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    /**
     * Teste le processus d'entrée d'un véhicule dans le parking lorsque l'obtention d'un emplacement disponible échoue.
     *
     * @throws Exception si une exception est levée pendant le test.
     */
    @Test
    public void testProcessIncomingVehicleError() throws Exception {
        // Configuration du comportement simulé pour l'objet mocké
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);

        // Appel de la méthode à tester
        parkingService.processIncomingVehicle();

        // Vérification des interactions avec l'objet mocké
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);

        // Vérification du résultat de la méthode
        assertEquals(-1, (int) parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }

    /**
     * Teste le processus de sortie d'un véhicule du parking lorsque tout se déroule comme prévu.
     *
     * @throws Exception si une exception est levée pendant le test.
     */
    @Test
    public void processExitingVehicleTest() throws Exception {
        // Configuration du comportement simulé pour l'objet mocké
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(ticketDAO.getNbTicket(any(String.class))).thenReturn(true);

        // Appel de la méthode à tester
        parkingService.processExitingVehicle();

        // Vérification des interactions avec les objets mockés
        verify(ticketDAO, Mockito.times(1)).getNbTicket(any(String.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    /**
     * Teste le processus de sortie d'un véhicule du parking lorsqu'il s'agit de sa première fois.
     *
     * @throws Exception si une exception est levée pendant le test.
     */
    @Test
    public void processExitingVehicleIfItsFirstTime() throws Exception {
        // Configuration du comportement simulé pour l'objet mocké
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(ticketDAO.getNbTicket(any(String.class))).thenReturn(false);

        // Appel de la méthode à tester
        parkingService.processExitingVehicle();

        // Vérification des interactions avec les objets mockés
        verify(ticketDAO, Mockito.times(1)).getNbTicket(any(String.class));
    }

    /**
     * Teste le processus de sortie d'un véhicule du parking lorsque la mise à jour du ticket échoue.
     *
     * @throws Exception si une exception est levée pendant le test.
     */
    @Test
    public void processExitingVehicleTestUnableUpdate() throws Exception {
        // Configuration du comportement simulé pour l'objet mocké
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.getNbTicket(any(String.class))).thenReturn(true);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        // Appel de la méthode à tester
        parkingService.processExitingVehicle();

        // Vérification des interactions avec les objets mockés
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
    }

    /**
     * Teste la récupération du prochain numéro de parking disponible pour un véhicule de type CAR.
     *
     * @throws Exception si une exception est levée pendant le test.
     */
    @Test
    public void testGetNextParkingNumberIfAvailable() {
        // Configuration du comportement simulé pour l'objet mocké
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        // Appel de la méthode à tester
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();

        // Vérification des interactions avec les objets mockés
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);

        // Vérification du résultat du test
        assertEquals(1, result.getId());
    }

    /**
     * Teste la récupération du prochain numéro de parking lorsque aucun emplacement n'est disponible.
     *
     * @throws Exception si une exception est levée pendant le test.
     */
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound() {
        // Configuration du comportement simulé pour l'objet mocké
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);

        // Appel de la méthode à tester
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();

        // Vérification des interactions avec les objets mockés
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);

        // Vérification du résultat du test
        assertNull(result);
    }

    /**
     * Teste la récupération du prochain numéro de parking avec un argument incorrect pour le type de véhicule.
     *
     * @throws Exception si une exception est levée pendant le test.
     */
    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        // Configuration du comportement simulé pour l'objet mocké
        when(inputReaderUtil.readSelection()).thenReturn(3);

        // Appel de la méthode à tester
        ParkingSpot result = parkingService.getNextParkingNumberIfAvailable();

        // Vérification des interactions avec l'objet mocké
        verify(inputReaderUtil, Mockito.times(1)).readSelection();

        // Vérification du résultat du test
        assertNull(result);
    }
}