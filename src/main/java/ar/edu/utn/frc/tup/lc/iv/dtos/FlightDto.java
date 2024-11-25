package ar.edu.utn.frc.tup.lc.iv.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightDto {
    private String id;
    private String aircraft;
    private LocalDateTime departure;
    private AirportDto airport;
    @JsonProperty("seat_map")
    private List<SeatDto> seatMap;

}
