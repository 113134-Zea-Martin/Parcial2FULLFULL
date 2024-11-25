package ar.edu.utn.frc.tup.lc.iv.services.reservationMapper;

import ar.edu.utn.frc.tup.lc.iv.dtos.PassengersDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.PassengerEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ReservationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationMapperTest {

    @InjectMocks
    private ReservationMapper reservationMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapEntityToDto() {

        FlightEntity flightEntity = FlightEntity.builder()
                .id("1")
                .build();

        PassengerEntity passengerEntity = PassengerEntity.builder()
                .name("John Doe")
                .seat(null)
                .build();

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .id("100")
                .status("CONFIRMED")
                .flight(flightEntity)
                .passengers(List.of(passengerEntity))
                .build();


        ReservationDto dto = reservationMapper.mapEntityToDto(reservationEntity);


        assertEquals(reservationEntity.getId(), dto.getId());
        assertEquals(reservationEntity.getStatus(), dto.getStatus());
        assertEquals(reservationEntity.getFlight().getId(), dto.getFlight());
        assertEquals(reservationEntity.getPassengers().size(), dto.getPassengers().size());
        assertEquals(reservationEntity.getPassengers().get(0).getName(), dto.getPassengers().get(0).getName());
    }

    @Test
    void testMapDtoToEntity() {

        FlightEntity flightEntity = FlightEntity.builder()
                .id("1")
                .build();

        PassengersDto passengersDto = PassengersDto.builder()
                .name("John Doe")
                .seat("1A")
                .build();

        ReservationDto dto = ReservationDto.builder()
                .id("100")
                .status("CONFIRMED")
                .flight("1")
                .passengers(List.of(passengersDto))
                .build();


        ReservationEntity reservationEntity = reservationMapper.mapDtoToEntity(dto, flightEntity);


        assertEquals(dto.getId(), reservationEntity.getId());
        assertEquals(dto.getStatus(), reservationEntity.getStatus());
        assertEquals(flightEntity, reservationEntity.getFlight());
        assertEquals(dto.getPassengers().size(), reservationEntity.getPassengers().size());
        assertEquals(dto.getPassengers().get(0).getName(), reservationEntity.getPassengers().get(0).getName());
        assertEquals(reservationEntity.getPassengers().get(0).getReservation(), reservationEntity);
    }
}
