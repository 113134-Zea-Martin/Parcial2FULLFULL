package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.FlightDto;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.services.flightMapper.FlightMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindFlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FindFlightService findFlightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFlightFound() {

        String flightNumber = "FL123";
        FlightEntity flightEntity = FlightEntity.builder()
                .id("FL123")
                .departure(LocalDateTime.now())
                .aircraft("Boeing 737")
                .build();
        FlightDto flightDto = FlightDto.builder()
                .id("FL123")
                .departure(LocalDateTime.now())
                .aircraft("Boeing 737")
                .build();

        when(flightRepository.findById(flightNumber)).thenReturn(Optional.of(flightEntity));
        when(flightMapper.mapFlightEntityToDto(flightEntity)).thenReturn(flightDto);


        FlightDto result = findFlightService.getFlight(flightNumber);


        assertNotNull(result);
        assertEquals(flightDto.getId(), result.getId());
        assertEquals(flightDto.getDeparture(), result.getDeparture());
        assertEquals(flightDto.getAircraft(), result.getAircraft());

        verify(flightRepository, times(1)).findById(flightNumber);
        verify(flightMapper, times(1)).mapFlightEntityToDto(flightEntity);
    }

    @Test
    void testGetFlightNotFound() {

        String flightNumber = "FL123";

        when(flightRepository.findById(flightNumber)).thenReturn(Optional.empty());


        CustomException exception = assertThrows(CustomException.class, () -> findFlightService.getFlight(flightNumber));
        assertEquals("Vuelo no encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(flightRepository, times(1)).findById(flightNumber);
        verifyNoInteractions(flightMapper);
    }
}
