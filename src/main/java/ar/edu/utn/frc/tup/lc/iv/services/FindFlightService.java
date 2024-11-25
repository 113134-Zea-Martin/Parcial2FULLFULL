package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.FlightDto;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.services.flightMapper.FlightMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindFlightService {
    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    public FlightDto getFlight(String flightNumber) {
        Optional<FlightEntity> flightEntity = flightRepository.findById(flightNumber);
        if (flightEntity.isEmpty()) {
            throw new CustomException("Vuelo no encontrado", HttpStatus.NOT_FOUND);
        }
        return flightMapper.mapFlightEntityToDto(flightEntity.get());
    }
}
