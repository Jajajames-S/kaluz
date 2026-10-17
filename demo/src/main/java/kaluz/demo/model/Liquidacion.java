package kaluz.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "liquidaciones")
@Getter @Setter @NoArgsConstructor
public class Liquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conductor_id", nullable = false)
    private Conductor conductor;

    @Column(name = "comision_acumulada_5", nullable = false, precision = 12, scale = 2)
    private BigDecimal comisionAcumulada5 = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 20)
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "fecha_pago")
    private Instant fechaPago;
}
