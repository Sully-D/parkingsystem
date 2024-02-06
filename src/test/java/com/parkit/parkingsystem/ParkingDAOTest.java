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

    @Test
    void getNextAvailableSlotDAO() {
        int getNextAvailableSlotReturn = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        assertTrue(getNextAvailableSlotReturn>=1);
    }

    @Test
    void getNextAvailableSlotDAOWithError() throws SQLException, ClassNotFoundException {
        DataBaseTestConfig mockDataBaseConfig = Mockito.mock(DataBaseTestConfig.class);
        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = mockDataBaseConfig;

        when(mockDataBaseConfig.getConnection()).thenThrow(new SQLException("Simulated database error"));

        int getNextAvailableSlotReturn = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        assertEquals(-1, getNextAvailableSlotReturn);
    }

    @Test
    void updateParkingDAO() {
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR, true);
        boolean updateParkingReturn = parkingSpotDAO.updateParking(parkingSpot);
        assertTrue(updateParkingReturn);
    }

    @Test
    void updateParkingDAOWithError() throws SQLException, ClassNotFoundException {
        DataBaseTestConfig mockDataBaseConfig = Mockito.mock(DataBaseTestConfig.class);
        parkingSpotDAO.dataBaseConfig = mockDataBaseConfig;

        when(mockDataBaseConfig.getConnection()).thenThrow(new SQLException("Simulated database error"));
        ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR, true);
        boolean updateParkingReturn = parkingSpotDAO.updateParking(parkingSpot);

        assertFalse(updateParkingReturn);
    }

}