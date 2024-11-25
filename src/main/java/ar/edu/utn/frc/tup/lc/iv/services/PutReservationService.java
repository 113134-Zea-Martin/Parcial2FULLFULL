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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PutReservationService {
    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;
    private final ReservationMapper reservationMapper;

    @Transactional
    public ReservationDto checkInReservation(ReservationDto reservationDto) {
        ReservationEntity reservationEntity = getReservation(reservationDto.getId());
        FlightEntity flightEntity = getFlight(reservationDto.getFlight());
        if(!reservationEntity.getStatus().equals("READY-TO-CHECK-IN")){
            throw new CustomException("La reserva no se encuentra disponible", HttpStatus.BAD_REQUEST);
        }
        reservationEntity.setStatus("CHECK-IN");
        //TODO VER vencimiento, no tengo nada eso no vi en ningun lado

        for (PassengersDto passengerDto : reservationDto.getPassengers()) {
            Optional<PassengerEntity> optPassenger = reservationEntity
                    .getPassengers().stream().filter(
                            m->m.getName().equals(passengerDto.getName())
                    ).findFirst();
            if(optPassenger.isEmpty()){
                throw new CustomException("El pasajero "+passengerDto.getName()+" no esta en la reserva origianl", HttpStatus.BAD_REQUEST);
            }
            PassengerEntity passengerEntity = optPassenger.get();
            Optional<SeatEntity> optSeat = flightEntity.getSeatMap().stream().filter(
                    m->m.getSeat().equals(passengerDto.getSeat())
            ).findFirst();
            if(optSeat.isEmpty()){
                throw  new CustomException("El asiento "+passengerDto.getSeat()+" no se encontro", HttpStatus.BAD_REQUEST);
            }
            SeatEntity seatEntity = optSeat.get();
            if (seatEntity.getStatus().equals("reserved")){
                throw  new CustomException("El asiento "+passengerDto.getSeat()+
                        " ya se encuentra reservado", HttpStatus.BAD_REQUEST);
            }
            seatEntity.setStatus("reserved");

            passengerEntity.setSeat(seatEntity);

        }
        reservationEntity=reservationRepository.save(reservationEntity);
        return reservationMapper.mapEntityToDto(reservationEntity);
    }

    public ReservationEntity getReservation(String reservationId) {
        Optional<ReservationEntity> optional = reservationRepository.findById(reservationId);
        if (optional.isEmpty()){
            throw new CustomException("No se encontro la reserva", HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }
    public FlightEntity getFlight(String flightId) {
        Optional<FlightEntity> optional = flightRepository.findById(flightId);
        if (optional.isEmpty()){
            throw new CustomException("No se encontro la flight", HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }
}
