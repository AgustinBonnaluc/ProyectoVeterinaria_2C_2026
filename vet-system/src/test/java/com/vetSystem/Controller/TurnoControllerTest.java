package com.vetSystem.Controller;

import com.vetSystem.DTO.TurnoRequestDTO;
import com.vetSystem.DTO.TurnoResponseDTO;
import com.vetSystem.Entity.EstadoTurno;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Exception.TurnoSuperpuestoException;
import com.vetSystem.Service.TurnoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TurnoController.class)
class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurnoService turnoService;

    // Fecha siempre futura: el test no se "vence" con el paso del tiempo
    private static final LocalDate FECHA_FUTURA = LocalDate.now().plusDays(30);
    private static final LocalTime HORA = LocalTime.of(10, 30);

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private TurnoResponseDTO turnoResponse(EstadoTurno estado) {
        return new TurnoResponseDTO(
                1L, FECHA_FUTURA, HORA, "Control anual", estado, null,
                1L, "Firulais", 1L, "Leandro");
    }

    private String bodyTurno(LocalDate fecha) {
        return """
                {
                  "fecha": "%s",
                  "hora": "10:30:00",
                  "motivo": "Control anual",
                  "mascotaId": 1,
                  "veterinarioId": 1
                }
                """.formatted(fecha);
    }

    // ---------------------------------------------------------------
    // GET
    // ---------------------------------------------------------------

    @Test
    void buscarPorId_cuandoNoExiste_retorna404ConErrorResponse() throws Exception {
        // Arrange
        when(turnoService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Turno", 99L));

        // Act
        ResultActions resultado = mockMvc.perform(get("/api/turno/99"));

        // Assert
        resultado.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensaje").value(containsString("99")))
                .andExpect(jsonPath("$.path").value("/api/turno/99"));
    }

    @Test
    void obtenerAgenda_conParametrosValidos_retorna200YConvierteLaFecha() throws Exception {
        // Arrange
        LocalDate fecha = LocalDate.of(2026, 12, 10);
        when(turnoService.obtenerAgenda(1L, fecha))
                .thenReturn(List.of(turnoResponse(EstadoTurno.PENDIENTE)));

        // Act
        ResultActions resultado = mockMvc.perform(get("/api/turno/agenda")
                .param("veterinarioId", "1")
                .param("fecha", "2026-12-10"));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].veterinarioNombre").value("Leandro"));

        verify(turnoService, times(1)).obtenerAgenda(1L, fecha);
    }

    // ---------------------------------------------------------------
    // POST
    // ---------------------------------------------------------------

    @Test
    void registrarTurno_cuandoBodyValido_retorna201EnEstadoPendiente() throws Exception {
        // Arrange
        when(turnoService.registrarTurno(any(TurnoRequestDTO.class)))
                .thenReturn(turnoResponse(EstadoTurno.PENDIENTE));

        // Act
        ResultActions resultado = mockMvc.perform(post("/api/turno")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyTurno(FECHA_FUTURA)));

        // Assert
        resultado.andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.mascotaNombre").value("Firulais"))
                .andExpect(jsonPath("$.veterinarioNombre").value("Leandro"));

        ArgumentCaptor<TurnoRequestDTO> captor = ArgumentCaptor.forClass(TurnoRequestDTO.class);
        verify(turnoService, times(1)).registrarTurno(captor.capture());

        TurnoRequestDTO recibido = captor.getValue();
        assertThat(recibido.getFecha()).isEqualTo(FECHA_FUTURA);
        assertThat(recibido.getHora()).isEqualTo(HORA);
        assertThat(recibido.getMascotaId()).isEqualTo(1L);
        assertThat(recibido.getVeterinarioId()).isEqualTo(1L);
    }

    @Test
    void registrarTurno_cuandoHaySuperposicion_retorna409ConErrorResponse() throws Exception {
        // Arrange
        when(turnoService.registrarTurno(any(TurnoRequestDTO.class)))
                .thenThrow(new TurnoSuperpuestoException("El veterinario ya tiene un turno en ese horario"));

        // Act
        ResultActions resultado = mockMvc.perform(post("/api/turno")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyTurno(FECHA_FUTURA)));

        // Assert
        resultado.andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.mensaje").value(containsString("ya tiene un turno")))
                .andExpect(jsonPath("$.path").value("/api/turno"));
    }

    @Test
    void registrarTurno_cuandoFechaPasada_retorna400YNoLlamaAlService() throws Exception {
        // Arrange
        String body = bodyTurno(LocalDate.of(2020, 1, 1));

        // Act
        ResultActions resultado = mockMvc.perform(post("/api/turno")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Assert
        resultado.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value(containsString("fecha")));

        verifyNoInteractions(turnoService);
    }

    @Test
    void registrarTurno_cuandoBodyVacio_retorna400ConTodosLosCamposObligatorios() throws Exception {
        // Arrange
        String body = "{}";

        // Act
        ResultActions resultado = mockMvc.perform(post("/api/turno")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // Assert
        resultado.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(allOf(
                        containsString("fecha"),
                        containsString("hora"),
                        containsString("motivo"),
                        containsString("mascotaId"),
                        containsString("veterinarioId"))));

        verifyNoInteractions(turnoService);
    }

    @Test
    void registrarTurno_cuandoMascotaNoExiste_retorna404() throws Exception {
        // Arrange
        when(turnoService.registrarTurno(any(TurnoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Mascota", 1L));

        // Act
        ResultActions resultado = mockMvc.perform(post("/api/turno")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyTurno(FECHA_FUTURA)));

        // Assert
        resultado.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(containsString("Mascota")));

        verify(turnoService, times(1)).registrarTurno(any(TurnoRequestDTO.class));
    }

    // ---------------------------------------------------------------
    // PATCH
    // ---------------------------------------------------------------

    @Test
    void actualizarEstado_conEstadoValido_retorna200YConvierteElEnum() throws Exception {
        // Arrange
        when(turnoService.actualizarEstado(1L, EstadoTurno.FINALIZADO, "Sin novedades"))
                .thenReturn(turnoResponse(EstadoTurno.FINALIZADO));

        // Act
        ResultActions resultado = mockMvc.perform(patch("/api/turno/1/estado")
                .param("estado", "FINALIZADO")
                .param("observaciones", "Sin novedades"));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADO"));

        verify(turnoService, times(1)).actualizarEstado(1L, EstadoTurno.FINALIZADO, "Sin novedades");
    }
}
