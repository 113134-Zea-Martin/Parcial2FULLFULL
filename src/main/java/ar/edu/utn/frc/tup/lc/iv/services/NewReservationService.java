package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ReservationEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ReservationRepository;
import ar.edu.utn.frc.tup.lc.iv.services.reservationMapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewReservationService {
    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;
    private final ReservationMapper reservationMapper;
    @Transactional
    public void addReservation(ReservationDto reservationDto){
        if(!validState(reservationDto.getStatus().toUpperCase())){
            throw new CustomException("El estado de la reserva no es valido", HttpStatus.BAD_REQUEST);
        }
        if(existeReservation(reservationDto)){
            throw new CustomException("La Reserva ya existe", HttpStatus.CONFLICT);
        }
        if(!hayLugar(reservationDto)){
            throw new CustomException("No hay lugar disponible", HttpStatus.CONFLICT);
        }
        FlightEntity flightEntity = getFlightEntity(reservationDto);
        ReservationEntity reservationEntity = reservationMapper.mapDtoToEntity(reservationDto,flightEntity);
        reservationRepository.save(reservationEntity);
    }
    public boolean hayLugar(ReservationDto reservationDto){
        FlightEntity flightEntity = getFlightEntity(reservationDto);
        Long count = flightEntity.getSeatMap().stream()
                .filter(m->m.getStatus().equals("available")).count();
        if(count < reservationDto.getPassengers().stream().count()){
            return false;
        }
        return true;
    }
    public boolean existeReservation(ReservationDto reservationDto){
        Optional<ReservationEntity> optional = reservationRepository.findById(reservationDto.getId());
        return optional.isPresent();
    }
    public FlightEntity getFlightEntity(ReservationDto reservationDto){
        Optional<FlightEntity> optFlightEntity = flightRepository.findById(reservationDto.getFlight());
        if(optFlightEntity.isEmpty()){
            throw new CustomException("La flight no existe", HttpStatus.NOT_FOUND);
        }
        return optFlightEntity.get();
    }
    public boolean validState(String state){
        return state.equals("READY-TO-CHECK-IN") || state.equals("CHECKED-IN") || state.equals("DUE");
    }
}
