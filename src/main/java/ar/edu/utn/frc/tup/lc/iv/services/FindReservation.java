package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.entities.ReservationEntity;
import ar.edu.utn.frc.tup.lc.iv.repositories.ReservationRepository;
import ar.edu.utn.frc.tup.lc.iv.services.reservationMapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindReservation {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    public ReservationDto getReservationById(String id) {
        Optional<ReservationEntity> optional = reservationRepository.findById(id);
        if(optional.isEmpty()){
            throw new CustomException("Reserva no encontradoa", HttpStatus.NOT_FOUND);
        }
        return reservationMapper.mapEntityToDto(optional.get());
    }
}
