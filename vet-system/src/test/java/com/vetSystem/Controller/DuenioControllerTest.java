package com.vetSystem.Controller;

import com.vetSystem.DTO.DuenioDTO;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Service.DuenioService;
import com.vetSystem.Service.MascotaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DuenioController.class)
class DuenioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DuenioService duenioService;

    @MockitoBean
    private MascotaService mascotaService; // DuenioController también depende de este

    @Test
    void listar_cuandoNoHayDuenios_retorna200ConListaVacia() throws Exception {
        // Arrange
        when(duenioService.listarTodos()).thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(get("/api/duenio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void buscarPorId_cuandoExiste_retorna200ConElDTO() throws Exception {
        // Arrange
        DuenioDTO dto = new DuenioDTO(1L, "Carlos", "Perez", "30111222", "1144445555", "carlos@mail.com");
        when(duenioService.buscarPorId(1L)).thenReturn(dto);

        // Act + Assert
        mockMvc.perform(get("/api/duenio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.cedula").value("30111222"))
                .andExpect(jsonPath("$.mascotas").doesNotExist());
    }

    @Test
    void buscarPorId_cuandoNoExiste_retorna404ConErrorResponse() throws Exception {
        // Arrange
        when(duenioService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Duenio", 99L));

        // Act + Assert
        mockMvc.perform(get("/api/duenio/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensaje").value(containsString("99")))
                .andExpect(jsonPath("$.path").value("/api/duenio/99"))
                .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void crear_cuandoBodyValido_retorna201() throws Exception {
        // Arrange
        String body = """
                {
                  "nombre": "Carlos",
                  "apellido": "Perez",
                  "cedula": "30111222",
                  "telefono": "1144445555",
                  "email": "carlos@mail.com"
                }
                """;
        DuenioDTO creado = new DuenioDTO(1L, "Carlos", "Perez", "30111222", "1144445555", "carlos@mail.com");
        when(duenioService.registrarDuenio(any(DuenioDTO.class))).thenReturn(creado);

        // Act + Assert
        mockMvc.perform(post("/api/duenio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(duenioService, times(1)).registrarDuenio(any(DuenioDTO.class));
    }

    @Test
    void crear_cuandoEmailVacio_retorna400YNoLlamaAlService() throws Exception {
        // Arrange
        String body = """
                {
                  "nombre": "Carlos",
                  "apellido": "Perez",
                  "cedula": "30111222",
                  "telefono": "1144445555",
                  "email": ""
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/api/duenio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value(containsString("email")));

        verifyNoInteractions(duenioService);
    }
}