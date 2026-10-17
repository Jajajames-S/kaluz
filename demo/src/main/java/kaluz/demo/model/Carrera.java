package kaluz.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "carreras")
@Getter @Setter @NoArgsConstructor
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conductor_id", nullable = false)
    private Conductor conductor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /** precio * 5 % (porcentaje configurable en kaluz.comision.porcentaje). */
    @Column(name = "comision_empresa", nullable = false, precision = 10, scale = 2)
    private BigDecimal comisionEmpresa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCarrera estado = EstadoCarrera.COMPLETADA;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
