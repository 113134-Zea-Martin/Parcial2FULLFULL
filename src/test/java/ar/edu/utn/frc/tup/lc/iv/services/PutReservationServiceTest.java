package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.PassengersDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.PassengerEntity;
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

class PutReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private PutReservationService putReservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckInReservationSuccess() {

        String reservationId = "RES123";
        String flightId = "FL123";

        PassengerEntity passengerEntity = PassengerEntity.builder()
                .name("John Doe")
                .build();

        SeatEntity seatEntity = SeatEntity.builder()
                .seat("1A")
                .status("available")
                .build();

        FlightEntity flightEntity = FlightEntity.builder()
                .id(flightId)
                .seatMap(List.of(seatEntity))
                .build();

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .id(reservationId)
                .status("READY-TO-CHECK-IN")
                .flight(flightEntity)
                .passengers(List.of(passengerEntity))
                .build();

        PassengersDto passengerDto = PassengersDto.builder()
                .name("John Doe")
                .seat("1A")
                .build();

        ReservationDto reservationDto = ReservationDto.builder()
                .id(reservationId)
                .flight(flightId)
                .status("READY-TO-CHECK-IN")
                .passengers(List.of(passengerDto))
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));
        when(reservationRepository.save(reservationEntity)).thenReturn(reservationEntity);
        when(reservationMapper.mapEntityToDto(reservationEntity)).thenReturn(reservationDto);


        ReservationDto result = putReservationService.checkInReservation(reservationDto);


        assertNotNull(result);
        assertEquals(reservationId, result.getId());
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(flightRepository, times(1)).findById(flightId);
        verify(reservationRepository, times(1)).save(reservationEntity);
        verify(reservationMapper, times(1)).mapEntityToDto(reservationEntity);
    }

    @Test
    void testCheckInReservationPassengerNotFound() {

        String reservationId = "RES123";
        String flightId = "FL123";

        FlightEntity flightEntity = FlightEntity.builder()
                .id(flightId)
                .seatMap(List.of(SeatEntity.builder().seat("1A").status("available").build()))
                .build();

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .id(reservationId)
                .status("READY-TO-CHECK-IN")
                .flight(flightEntity)
                .passengers(List.of(PassengerEntity.builder().name("Jane Doe").build()))
                .build();

        PassengersDto passengerDto = PassengersDto.builder()
                .name("John Doe")
                .seat("1A")
                .build();

        ReservationDto reservationDto = ReservationDto.builder()
                .id(reservationId)
                .flight(flightId)
                .passengers(List.of(passengerDto))
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));


        CustomException exception = assertThrows(CustomException.class, () -> putReservationService.checkInReservation(reservationDto));
        assertEquals("El pasajero John Doe no esta en la reserva origianl", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testCheckInReservationSeatUnavailable() {

        String reservationId = "RES123";
        String flightId = "FL123";

        SeatEntity seatEntity = SeatEntity.builder()
                .seat("1A")
                .status("reserved")
                .build();

        FlightEntity flightEntity = FlightEntity.builder()
                .id(flightId)
                .seatMap(List.of(seatEntity))
                .build();

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .id(reservationId)
                .status("READY-TO-CHECK-IN")
                .flight(flightEntity)
                .passengers(List.of(PassengerEntity.builder().name("John Doe").build()))
                .build();

        PassengersDto passengerDto = PassengersDto.builder()
                .name("John Doe")
                .seat("1A")
                .build();

        ReservationDto reservationDto = ReservationDto.builder()
                .id(reservationId)
                .flight(flightId)
                .passengers(List.of(passengerDto))
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flightEntity));


        CustomException exception = assertThrows(CustomException.class, () -> putReservationService.checkInReservation(reservationDto));
        assertEquals("El asiento 1A ya se encuentra reservado", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
