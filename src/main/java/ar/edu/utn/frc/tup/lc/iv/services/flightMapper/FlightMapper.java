package ar.edu.utn.frc.tup.lc.iv.services.flightMapper;

import ar.edu.utn.frc.tup.lc.iv.dtos.AirportDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.FlightDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.SeatDto;
import ar.edu.utn.frc.tup.lc.iv.entities.AirportEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.SeatEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FlightMapper {
    public FlightEntity mapFlightDtoToEntity(FlightDto flightDto) {
        FlightEntity flightEntity = FlightEntity.builder()
                .departure(flightDto.getDeparture())
                .id(flightDto.getId())
                .aircraft(flightDto.getAircraft())
                .seatMap(new ArrayList<>())
                .airport(AirportEntity.builder()
                        .code(flightDto.getAirport().getCode())
                        .name(flightDto.getAirport().getName())
                        .location(flightDto.getAirport().getLocation())
                        .build())
                .build();
        for (SeatDto seatDto : flightDto.getSeatMap()){
            SeatEntity seatEntity = SeatEntity.builder()
                    .flight(flightEntity)
                    .seat(seatDto.getSeat())
                    .status(seatDto.getStatus())
                    .build();
            flightEntity.getSeatMap().add(seatEntity);
        }
        return flightEntity;

    }
    public FlightDto mapFlightEntityToDto(FlightEntity flightEntity) {
        FlightDto flightDto = FlightDto.builder()
                .departure(flightEntity.getDeparture())
                .id(flightEntity.getId())
                .aircraft(flightEntity.getAircraft())
                .seatMap(new ArrayList<>())
                .airport(AirportDto.builder()
                        .code(flightEntity.getAirport().getCode())
                        .name(flightEntity.getAirport().getName())
                        .location(flightEntity.getAirport().getLocation())
                        .build())
                .build();
        for (SeatEntity seatEntity : flightEntity.getSeatMap()){
            SeatDto seatDto = SeatDto.builder()
                    .seat(seatEntity.getSeat())
                    .status(seatEntity.getStatus())
                    .build();
            flightDto.getSeatMap().add(seatDto);
        }
        return flightDto;
    }
}
