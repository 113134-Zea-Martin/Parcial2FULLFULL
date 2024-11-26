package ar.edu.utn.frc.tup.lc.iv.services.reservationMapper;

import ar.edu.utn.frc.tup.lc.iv.dtos.PassengersDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.PassengerEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ReservationEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
public class ReservationMapper {
    public ReservationDto mapEntityToDto(ReservationEntity reservationEntity) {
        ReservationDto dto = ReservationDto.builder()
                .id(reservationEntity.getId())
                .status(reservationEntity.getStatus())
                .flight(reservationEntity.getFlight().getId())
                .passengers(new ArrayList<>())
                .build();
        for (PassengerEntity passengerEntity : reservationEntity.getPassengers()) {
            PassengersDto passengersDto = PassengersDto.builder()
                    .name(passengerEntity.getName())
                    .build();
            if(passengerEntity.getSeat() != null)
            {
                passengersDto.setSeat(passengerEntity.getSeat().getSeat());
            }else{
                passengersDto.setSeat("");
            }
            dto.getPassengers().add(passengersDto);
        }
        return dto;
    }
    public ReservationEntity mapDtoToEntity(ReservationDto dto,FlightEntity flightEntity) {
        ReservationEntity reservationEntity = ReservationEntity.builder()
                .flight(flightEntity)
                .passengers(new ArrayList<>())
                .id(dto.getId())
                .status(dto.getStatus())
                .build();
        for (PassengersDto passengersDto : dto.getPassengers()) {
            PassengerEntity passengerEntity = PassengerEntity.builder()
                    .name(passengersDto.getName())
                    .reservation(reservationEntity)
                    .build();
            reservationEntity.getPassengers().add(passengerEntity);
        }
        return reservationEntity;
    }
}
