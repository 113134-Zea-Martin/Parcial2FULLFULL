package ar.edu.utn.frc.tup.lc.iv.services.flightMapper;

import ar.edu.utn.frc.tup.lc.iv.dtos.AirportDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.FlightDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.SeatDto;
import ar.edu.utn.frc.tup.lc.iv.entities.AirportEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.SeatEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

class FlightMapperTest {

    @InjectMocks
    private FlightMapper flightMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapFlightDtoToEntity() {

        AirportDto airportDto = AirportDto.builder()
                .code("EZE")
                .name("Ezeiza International Airport")
                .location("Buenos Aires")
                .build();

        SeatDto seatDto = SeatDto.builder()
                .seat("1A")
                .status("AVAILABLE")
                .build();

        FlightDto flightDto = FlightDto.builder()
                .id("1")
                .departure(LocalDateTime.now())
                .aircraft("Boeing 737")
                .airport(airportDto)
                .seatMap(List.of(seatDto))
                .build();


        FlightEntity flightEntity = flightMapper.mapFlightDtoToEntity(flightDto);


        assertEquals(flightDto.getId(), flightEntity.getId());
        assertEquals(flightDto.getDeparture(), flightEntity.getDeparture());
        assertEquals(flightDto.getAircraft(), flightEntity.getAircraft());
        assertEquals(flightDto.getAirport().getCode(), flightEntity.getAirport().getCode());
        assertEquals(flightDto.getAirport().getName(), flightEntity.getAirport().getName());
        assertEquals(flightDto.getAirport().getLocation(), flightEntity.getAirport().getLocation());
        assertEquals(flightDto.getSeatMap().size(), flightEntity.getSeatMap().size());
        assertEquals(flightDto.getSeatMap().get(0).getSeat(), flightEntity.getSeatMap().get(0).getSeat());
    }

    @Test
    void testMapFlightEntityToDto() {

        AirportEntity airportEntity = AirportEntity.builder()
                .code("EZE")
                .name("Ezeiza International Airport")
                .location("Buenos Aires")
                .build();

        SeatEntity seatEntity = SeatEntity.builder()
                .seat("1A")
                .status("AVAILABLE")
                .build();

        FlightEntity flightEntity = FlightEntity.builder()
                .id("1")
                .departure(LocalDateTime.now())
                .aircraft("Boeing 737")
                .airport(airportEntity)
                .seatMap(List.of(seatEntity))
                .build();


        FlightDto flightDto = flightMapper.mapFlightEntityToDto(flightEntity);


        assertEquals(flightEntity.getId(), flightDto.getId());
        assertEquals(flightEntity.getDeparture(), flightDto.getDeparture());
        assertEquals(flightEntity.getAircraft(), flightDto.getAircraft());
        assertEquals(flightEntity.getAirport().getCode(), flightDto.getAirport().getCode());
        assertEquals(flightEntity.getAirport().getName(), flightDto.getAirport().getName());
        assertEquals(flightEntity.getAirport().getLocation(), flightDto.getAirport().getLocation());
        assertEquals(flightEntity.getSeatMap().size(), flightDto.getSeatMap().size());
        assertEquals(flightEntity.getSeatMap().get(0).getSeat(), flightDto.getSeatMap().get(0).getSeat());
    }
}
