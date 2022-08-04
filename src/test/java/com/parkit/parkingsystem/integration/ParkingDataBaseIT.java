package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;



import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    

   // private static DataBasePrepareService dataBasePrepareService;
    
   
   
    @Mock
    private static InputReaderUtil inputReaderUtil;
    private String regNumberTested="WXYZ";


    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
      //  dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumberTested);
       // dataBasePrepareService.clearDataBaseEntries();
        
    }

    @AfterAll

    private static void tearDown() {

    }

    @Test
    public void testParkingACar() {
    	//WHEN
    	
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket(regNumberTested);
        String sut = ticket.getVehicleRegNumber();
        Boolean sut2 = ticket.getParkingSpot().isAvailable();

 
        assertEquals(regNumberTested, sut);
        assertEquals(false, sut2);

    }
    
    @Test
    public void testParkingACarRecurringUser() {
    	//WHEN
    	System.setOut(new PrintStream(outputStreamCaptor));
    	when(inputReaderUtil.readSelection()).thenReturn(1);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        assertEquals("Hello Baeldung Readers!!", outputStreamCaptor.toString()
          .trim());
        

    }
    
    @Test
    public void testParkingLotExit() {
    	 ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
         parkingService.processExitingVehicle();
         Ticket ticket = ticketDAO.getTicket(regNumberTested);
         
         DateFormat dateFormatUsed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
         Date outTime = new Date();
         Date outTimeNearestMin = DateUtils.round(outTime, Calendar.MINUTE);
         String strDateEx = dateFormatUsed.format(outTimeNearestMin);
         Date realTime = ticket.getOutTime();
         Date realTimeNearestMin=DateUtils.round(realTime, Calendar.MINUTE);
         String strDateReal = dateFormatUsed.format(realTimeNearestMin);

         
         Double realPrice=ticket.getPrice();
         long inTime=ticket.getInTime().getTime();
         long duration=outTime.getTime()-inTime;
         Long exPrice1 =Math.round(1.5*(duration*(1/3.6)*1e-6));
         Double exPrice =exPrice1.doubleValue();
         double eps = 1e-03;
       
         assertEquals(strDateEx,strDateReal);
         assertEquals(exPrice,realPrice,eps);
    }

    
    

}

