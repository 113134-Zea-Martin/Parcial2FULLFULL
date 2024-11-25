package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.entities.ReservationEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.ReservationRepository;
import ar.edu.utn.frc.tup.lc.iv.services.reservationMapper.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindReservationTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private FindReservation findReservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetReservationByIdFound() {

        String reservationId = "R123";
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .id(reservationId)
                .status("CONFIRMED")
                .build();
        ReservationDto reservationDto = ReservationDto.builder()
                .id(reservationId)
                .status("CONFIRMED")
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(reservationMapper.mapEntityToDto(reservationEntity)).thenReturn(reservationDto);


        ReservationDto result = findReservation.getReservationById(reservationId);


        assertNotNull(result);
        assertEquals(reservationId, result.getId());
        assertEquals("CONFIRMED", result.getStatus());

        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationMapper, times(1)).mapEntityToDto(reservationEntity);
    }

    @Test
    void testGetReservationByIdNotFound() {

        String reservationId = "R123";

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());


        CustomException exception = assertThrows(CustomException.class, () -> findReservation.getReservationById(reservationId));
        assertEquals("Reserva no encontradoa", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(reservationRepository, times(1)).findById(reservationId);
        verifyNoInteractions(reservationMapper);
    }
}
