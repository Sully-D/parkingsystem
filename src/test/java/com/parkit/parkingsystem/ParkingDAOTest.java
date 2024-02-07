package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.util.InputReaderUtil;

import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class ParkingDAOTest {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;


    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;

    }

    /**
     * Test case for retrieving the next available parking slot for a specific parking type from the DAO.
     * This test checks if the returned slot number is greater than or equal to 1, indicating a valid available slot.
     */
    @Test
    void getNextAvailableSlotDAO() {
        // Call the DAO method to get the next available slot for CAR parking type
        int getNextAvailableSlotReturn = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // Assertion to check if the returned slot number is greater than or equal to 1
        assertTrue(getNextAvailableSlotReturn >= 1);
    }

    /**
     * Test case for updating parking information in the DAO.
     * This test checks if the parking information for a given parking spot is successfully updated in the DAO.
     */
    @Test
    void updateParkingDAO() {
        // Create a ParkingSpot instance with specific details for testing
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        // Call the DAO method to update parking information and get the return status
        boolean updateParkingReturn = parkingSpotDAO.updateParking(parkingSpot);

        // Assertion to check if the parking information is successfully updated in the DAO
        assertTrue(updateParkingReturn);
    }

    /**
     * Test case for handling an error scenario when retrieving the next available parking slot from the DAO.
     * This test simulates a database error and ensures that the DAO method returns -1 in such cases.
     *
     * @throws SQLException if there is an issue with the simulated database connection.
     * @throws ClassNotFoundException if the database driver class is not found during the simulation.
     */
    @Test
    void getNextAvailableSlotDAOWithError() throws SQLException, ClassNotFoundException {
        // Create a mock for the database configuration
        DataBaseTestConfig mockDataBaseConfig = Mockito.mock(DataBaseTestConfig.class);

        // Create a ParkingSpotDAO instance and set the mock database configuration
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = mockDataBaseConfig;

        // Simulate a database error when getConnection() is called on the mock database configuration
        when(mockDataBaseConfig.getConnection()).thenThrow(new SQLException("Simulated database error"));

        // Call the DAO method to get the next available slot for CAR parking type in the error scenario
        int getNextAvailableSlotReturn = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // Assertion to check if the DAO method returns -1 in case of a simulated database error
        assertEquals(-1, getNextAvailableSlotReturn);
    }

    /**
     * Test case for updating parking information in the DAO with a simulated database error.
     * This test checks if the DAO handles a database error gracefully and returns false.
     *
     * @throws SQLException            Simulated database error.
     * @throws ClassNotFoundException   Simulated database error.
     */
    @Test
    void updateParkingDAOWithError() throws SQLException, ClassNotFoundException {
        // Create a mock for the database configuration
        DataBaseTestConfig mockDataBaseConfig = Mockito.mock(DataBaseTestConfig.class);
        // Set the mock configuration in the DAO
        parkingSpotDAO.dataBaseConfig = mockDataBaseConfig;

        // Simulate a database error when attempting to get a connection
        when(mockDataBaseConfig.getConnection()).thenThrow(new SQLException("Simulated database error"));

        // Create a ParkingSpot instance with specific details for testing
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

        // Call the DAO method to update parking information and get the return status
        boolean updateParkingReturn = parkingSpotDAO.updateParking(parkingSpot);

        // Assertion to check if the return status is false, indicating a handling of database error
        assertFalse(updateParkingReturn);
    }

}