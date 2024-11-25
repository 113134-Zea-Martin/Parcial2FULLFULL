package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.FlightDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.SeatDto;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewFlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private NewFlightService newFlightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddFlightSuccess() {

        FlightDto flightDto = FlightDto.builder()
                .id("FL123")
                .departure(LocalDateTime.now().plusHours(7))
                .aircraft("Boeing 737")
                .seatMap(List.of(SeatDto.builder().seat("1A").status("available").build()))
                .build();

        FlightEntity flightEntity = FlightEntity.builder()
                .id("FL123")
                .departure(flightDto.getDeparture())
                .aircraft(flightDto.getAircraft())
                .build();

        when(flightRepository.findById(flightDto.getId())).thenReturn(Optional.empty());
        when(flightMapper.mapFlightDtoToEntity(flightDto)).thenReturn(flightEntity);
        when(flightRepository.save(flightEntity)).thenReturn(flightEntity);
        when(flightMapper.mapFlightEntityToDto(flightEntity)).thenReturn(flightDto);


        FlightDto result = newFlightService.addFlight(flightDto);


        assertNotNull(result);
        assertEquals(flightDto.getId(), result.getId());
        assertEquals(flightDto.getDeparture(), result.getDeparture());

        verify(flightRepository, times(1)).findById(flightDto.getId());
        verify(flightMapper, times(1)).mapFlightDtoToEntity(flightDto);
        verify(flightRepository, times(1)).save(flightEntity);
        verify(flightMapper, times(1)).mapFlightEntityToDto(flightEntity);
    }

    @Test
    void testAddFlightInvalidDeparture() {

        FlightDto flightDto = FlightDto.builder()
                .id("FL123")
                .departure(LocalDateTime.now().plusHours(5))
                .aircraft("Boeing 737")
                .seatMap(List.of())
                .build();


        CustomException exception = assertThrows(CustomException.class, () -> newFlightService.addFlight(flightDto));
        assertEquals("La hora del vuelo debe ser sea mayor a la fecha actual al menos en 6 horas", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verifyNoInteractions(flightMapper);
        verifyNoInteractions(flightRepository);
    }

    @Test
    void testAddFlightAlreadyExists() {

        FlightDto flightDto = FlightDto.builder()
                .id("FL123")
                .departure(LocalDateTime.now().plusHours(7))
                .aircraft("Boeing 737")
                .seatMap(List.of())
                .build();

        when(flightRepository.findById(flightDto.getId())).thenReturn(Optional.of(new FlightEntity()));


        CustomException exception = assertThrows(CustomException.class, () -> newFlightService.addFlight(flightDto));
        assertEquals("El flight ya existe", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(flightRepository, times(1)).findById(flightDto.getId());
        verifyNoInteractions(flightMapper);
    }

    @Test
    void testAddFlightInvalidSeatStatus() {

        FlightDto flightDto = FlightDto.builder()
                .id("FL123")
                .departure(LocalDateTime.now().plusHours(7))
                .aircraft("Boeing 737")
                .seatMap(List.of(SeatDto.builder().seat("1A").status("wrong_status").build()))
                .build();

        when(flightRepository.findById(flightDto.getId())).thenReturn(Optional.empty());


        CustomException exception = assertThrows(CustomException.class, () -> newFlightService.addFlight(flightDto));
        assertEquals("El seat contiene un estado incorrecto", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(flightRepository, times(1)).findById(flightDto.getId());
        verifyNoInteractions(flightMapper);
    }

    @Test
    void testValidateDeparture() {

        boolean isValid = newFlightService.validateDeparture(LocalDateTime.now().plusHours(7));


        assertTrue(isValid);
    }

    @Test
    void testValidateDepartureInvalid() {

        boolean isValid = newFlightService.validateDeparture(LocalDateTime.now().plusHours(5));


        assertFalse(isValid);
    }

    @Test
    void testValidateSeatStatus() {

        assertTrue(newFlightService.validateSeatStatus("available"));
        assertTrue(newFlightService.validateSeatStatus("reserved"));
        assertFalse(newFlightService.validateSeatStatus("invalid_status"));
    }
}
