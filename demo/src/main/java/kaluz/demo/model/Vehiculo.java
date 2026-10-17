package kaluz.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "vehiculos")
@Getter @Setter @NoArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVehiculo tipo;

    @Column(length = 60)
    private String marca;

    @ManyToOne
    @JoinColumn(name = "conductor_id")
    private Conductor conductor;

    private Double latitud;

    private Double longitud;

    @Column(name = "ultima_actualizacion_gps")
    private Instant ultimaActualizacionGps;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
