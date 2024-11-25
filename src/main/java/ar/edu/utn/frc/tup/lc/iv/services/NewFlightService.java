package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.FlightDto;
import ar.edu.utn.frc.tup.lc.iv.dtos.SeatDto;
import ar.edu.utn.frc.tup.lc.iv.entities.AirportEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.SeatEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.services.flightMapper.FlightMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewFlightService {
    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    @Transactional
    public FlightDto addFlight(FlightDto flightDto) {
        if (!validateDeparture(flightDto.getDeparture())) {
            throw new CustomException("La hora del vuelo debe ser sea mayor a " +
                    "la fecha actual al menos en 6 horas", HttpStatus.BAD_REQUEST);
        }
        if(existsFlight(flightDto.getId())){
            throw new CustomException("El flight ya existe", HttpStatus.CONFLICT);
        }
        for (SeatDto seatDto : flightDto.getSeatMap()){
            seatDto.setStatus(seatDto.getStatus().toLowerCase(Locale.ROOT));
            if (!validateSeatStatus(seatDto.getStatus())){
                throw new CustomException("El seat contiene un estado incorrecto", HttpStatus.CONFLICT);
            }
        }
        FlightEntity flightEntity = flightMapper.mapFlightDtoToEntity(flightDto);
        flightEntity = flightRepository.save(flightEntity);
        return flightMapper.mapFlightEntityToDto(flightEntity);
    }
    public boolean existsFlight(String flightNumber) {

        Optional<FlightEntity> optionalFlightEntity = flightRepository.findById(flightNumber);
        return optionalFlightEntity.isPresent();
    }
    public boolean validateDeparture(LocalDateTime departure) {
        LocalDateTime fechaMinima = LocalDateTime.now().plusHours(6);

        return departure.isAfter(fechaMinima);
    }
    public boolean validateSeatStatus(String seatStatus) {
        return seatStatus.equals("reserved") || seatStatus.equals("available");
    }

}
