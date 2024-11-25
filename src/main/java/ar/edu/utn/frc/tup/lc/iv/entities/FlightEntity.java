package ar.edu.utn.frc.tup.lc.iv.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "flights")
public class FlightEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String aircraft;

    @Column(nullable = false)
    private LocalDateTime departure;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "airport_id", referencedColumnName = "code", nullable = false)
    private AirportEntity airport;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatEntity> seatMap;
}
