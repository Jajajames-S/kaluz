package kaluz.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class KaluzApiTests {

    @Autowired WebApplicationContext ctx;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    private static long idOf(MvcResult r, String regex) throws Exception {
        Matcher m = Pattern.compile(regex).matcher(r.getResponse().getContentAsString());
        if (!m.find()) throw new AssertionError("no match in " + r.getResponse().getContentAsString());
        return Long.parseLong(m.group(1));
    }

    private String registro(String dni, String placa, LocalDate soat) {
        return """
            {"dni":"%s","nombres":"Luis","apellidos":"Quispe","telefono":"964111222",
             "vehiculo":{"placa":"%s","tipo":"TAXI","marca":"Toyota"},
             "documentos":[{"tipoDoc":"SOAT","fechaVencimiento":"%s"}]}
            """.formatted(dni, placa, soat);
    }

    @Test
    void flujoCompleto_registro_gps_cercano_carrera_liquidacion_alertas() throws Exception {
        MvcResult reg = mvc.perform(post("/api/conductores/registro").contentType(MediaType.APPLICATION_JSON)
                        .content(registro("11111111", "AAA-111", LocalDate.now().minusDays(1))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentos[0].estadoDoc").value("VENCIDO"))
                .andReturn();
        long conductorId = idOf(reg, "\"conductor\":\\{\"id\":(\\d+)");
        long vehiculoId = idOf(reg, "\"vehiculo\":\\{\"id\":(\\d+)");

        // DNI duplicado -> 409
        mvc.perform(post("/api/conductores/registro").contentType(MediaType.APPLICATION_JSON)
                        .content(registro("11111111", "BBB-222", LocalDate.now().plusDays(100))))
                .andExpect(status().isConflict());

        // GPS + cercano
        mvc.perform(put("/api/vehiculos/{id}/gps", vehiculoId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"latitud\":-12.0651,\"longitud\":-75.2049}"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/vehiculos/cercanos").param("lat", "-12.0660").param("lng", "-75.2050"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehiculo.placa").value("AAA-111"))
                .andExpect(jsonPath("$.distanciaKm", lessThan(0.5)));

        // Carrera: 5 % de 20.00 = 1.00 ; 5 % de 13.50 = 0.68 (HALF_UP de 0.675)
        mvc.perform(post("/api/carreras").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conductorId\":" + conductorId + ",\"precio\":20.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comisionEmpresa").value(1.00));
        mvc.perform(post("/api/carreras").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conductorId\":" + conductorId + ",\"precio\":13.50}"))
                .andExpect(jsonPath("$.comisionEmpresa").value(0.68));

        mvc.perform(get("/api/liquidaciones/conductor/{id}", conductorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPendiente").value(1.68))
                .andExpect(jsonPath("$.liquidaciones", hasSize(1)));

        mvc.perform(get("/api/documentos/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.placa=='AAA-111')].estadoDoc", contains("VENCIDO")));
    }

    @Test
    void validaciones() throws Exception {
        mvc.perform(post("/api/conductores/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dni\":\"12\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.dni").exists());
        mvc.perform(post("/api/carreras").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conductorId\":999999,\"precio\":10}"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/vehiculos/cercanos").param("lat", "abc").param("lng", "1"))
                .andExpect(status().isBadRequest());
    }
}
