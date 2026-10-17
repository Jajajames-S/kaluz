package kaluz.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import kaluz.demo.model.EstadoCarrera;
import kaluz.demo.model.TipoDocumento;
import kaluz.demo.model.TipoVehiculo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class Requests {
    private Requests() {}

    public record VehiculoRequest(
            @NotBlank @Size(max = 10) String placa,
            @NotNull TipoVehiculo tipo,
            @Size(max = 60) String marca) {}

    public record DocumentoRequest(
            @NotNull TipoDocumento tipoDoc,
            @NotNull LocalDate fechaVencimiento) {}

    public record RegistroConductorRequest(
            @NotBlank @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 digitos") String dni,
            @NotBlank @Size(max = 100) String nombres,
            @NotBlank @Size(max = 100) String apellidos,
            @NotBlank @Size(max = 20) String telefono,
            @NotNull @Valid VehiculoRequest vehiculo,
            @Valid List<DocumentoRequest> documentos) {}

    public record DocumentoCreateRequest(
            @NotNull Long vehiculoId,
            @NotNull TipoDocumento tipoDoc,
            @NotNull LocalDate fechaVencimiento) {}

    public record GpsRequest(
            @NotNull @DecimalMin("-90") @DecimalMax("90") Double latitud,
            @NotNull @DecimalMin("-180") @DecimalMax("180") Double longitud) {}

    public record CarreraRequest(
            @NotNull Long conductorId,
            @NotNull @DecimalMin(value = "0.01") @Digits(integer = 8, fraction = 2) BigDecimal precio,
            EstadoCarrera estado) {}
}
