package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.ReservationDto;
import ar.edu.utn.frc.tup.lc.iv.services.FindReservation;
import ar.edu.utn.frc.tup.lc.iv.services.NewReservationService;
import ar.edu.utn.frc.tup.lc.iv.services.PutReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final NewReservationService newReservationService;
    private final FindReservation findReservation;
    private final PutReservationService putReservationService;
    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable String id) {
        ReservationDto reservationDto = findReservation.getReservationById(id);
        return ResponseEntity.ok(reservationDto);
    }
    @PostMapping("/reservations")
    public ResponseEntity createReservation(@RequestBody ReservationDto reservationDto) {
        newReservationService.addReservation(reservationDto);
        return ResponseEntity.ok(null);
    }
    @PutMapping("/reservations")
    public ResponseEntity<ReservationDto> updateReservation(@RequestBody ReservationDto reservationDto) {
        ReservationDto updatedReservation = putReservationService.checkInReservation(reservationDto);
        return ResponseEntity.ok(updatedReservation);
    }

}
