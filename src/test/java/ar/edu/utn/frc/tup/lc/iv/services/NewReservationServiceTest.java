package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.PassengersDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ReservationEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.SeatEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ReservationRepository;
import ar.edu.utn.frc.tup.lc.iv.services.reservationMapper.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private NewReservationService newReservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddReservationSuccess() {

        ReservationDto reservationDto = ReservationDto.builder()
                .id("RES123")
                .status("READY-TO-CHECK-IN")
                .flight("FL123")
                .passengers(List.of(PassengersDto.builder().name("John Doe").build()))
                .build();

        FlightEntity flightEntity = FlightEntity.builder()
                .id("FL123")
                .seatMap(List.of(
                        SeatEntity.builder().seat("1A").status("available").build()
                ))
                .build();

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .id("RES123")
                .status("READY-TO-CHECK-IN")
                .flight(flightEntity)
                .build();

        when(flightRepository.findById(reservationDto.getFlight())).thenReturn(Optional.of(flightEntity));
        when(reservationRepository.findById(reservationDto.getId())).thenReturn(Optional.empty());
        when(reservationMapper.mapDtoToEntity(reservationDto, flightEntity)).thenReturn(reservationEntity);


        newReservationService.addReservation(reservationDto);


        verify(reservationRepository, times(1)).findById(reservationDto.getId());
        verify(reservationRepository, times(1)).save(reservationEntity);
        verify(reservationMapper, times(1)).mapDtoToEntity(reservationDto, flightEntity);
    }

    @Test
    void testAddReservationInvalidStatus() {

        ReservationDto reservationDto = ReservationDto.builder()
                .id("RES123")
                .status("INVALID-STATUS")
                .flight("FL123")
                .passengers(List.of())
                .build();


        CustomException exception = assertThrows(CustomException.class, () -> newReservationService.addReservation(reservationDto));
        assertEquals("El estado de la reserva no es valido", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verifyNoInteractions(flightRepository);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void testAddReservationAlreadyExists() {

        ReservationDto reservationDto = ReservationDto.builder()
                .id("RES123")
                .status("READY-TO-CHECK-IN")
                .flight("FL123")
                .passengers(List.of())
                .build();

        when(reservationRepository.findById(reservationDto.getId())).thenReturn(Optional.of(new ReservationEntity()));


        CustomException exception = assertThrows(CustomException.class, () -> newReservationService.addReservation(reservationDto));
        assertEquals("La Reserva ya existe", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(reservationRepository, times(1)).findById(reservationDto.getId());
        verifyNoInteractions(flightRepository);
    }

    @Test
    void testAddReservationNoSeatsAvailable() {

        ReservationDto reservationDto = ReservationDto.builder()
                .id("RES123")
                .status("READY-TO-CHECK-IN")
                .flight("FL123")
                .passengers(List.of(PassengersDto.builder().name("John Doe").build()))
                .build();

        FlightEntity flightEntity = FlightEntity.builder()
                .id("FL123")
                .seatMap(List.of(
                        SeatEntity.builder().seat("1A").status("reserved").build()
                ))
                .build();

        when(flightRepository.findById(reservationDto.getFlight())).thenReturn(Optional.of(flightEntity));
        when(reservationRepository.findById(reservationDto.getId())).thenReturn(Optional.empty());


        CustomException exception = assertThrows(CustomException.class, () -> newReservationService.addReservation(reservationDto));
        assertEquals("No hay lugar disponible", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(flightRepository, times(1)).findById(reservationDto.getFlight());
    }

    @Test
    void testGetFlightEntityFlightNotFound() {

        ReservationDto reservationDto = ReservationDto.builder()
                .flight("FL123")
                .build();

        when(flightRepository.findById(reservationDto.getFlight())).thenReturn(Optional.empty());


        CustomException exception = assertThrows(CustomException.class, () -> newReservationService.getFlightEntity(reservationDto));
        assertEquals("La flight no existe", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(flightRepository, times(1)).findById(reservationDto.getFlight());
    }

    @Test
    void testValidState() {

        assertTrue(newReservationService.validState("READY-TO-CHECK-IN"));
        assertTrue(newReservationService.validState("CHECKED-IN"));
        assertTrue(newReservationService.validState("DUE"));
        assertFalse(newReservationService.validState("INVALID-STATE"));
    }
}
