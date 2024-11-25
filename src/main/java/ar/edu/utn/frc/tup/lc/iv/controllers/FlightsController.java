package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.FlightDto;
import ar.edu.utn.frc.tup.lc.iv.services.FindFlightService;
import ar.edu.utn.frc.tup.lc.iv.services.NewFlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FlightsController {
    private final NewFlightService newFlightService;
    private final FindFlightService findFlightService;
    @PostMapping("/flights")
    public ResponseEntity<FlightDto> postFlight(@RequestBody FlightDto flight){
        FlightDto newFlightDto = newFlightService.addFlight(flight);
        return ResponseEntity.ok(newFlightDto);
    }
    @GetMapping("/flights/{id}")
    public ResponseEntity<FlightDto> getFlightById(@PathVariable String id){
        FlightDto flightDto = findFlightService.getFlight(id);
        return ResponseEntity.ok(flightDto);
    }
}
