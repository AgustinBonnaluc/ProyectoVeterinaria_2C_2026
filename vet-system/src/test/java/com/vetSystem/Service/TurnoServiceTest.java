package com.vetSystem.Service;

import com.vetSystem.DTO.TurnoRequestDTO;
import com.vetSystem.DTO.TurnoResponseDTO;
import com.vetSystem.Entity.EstadoTurno;
import com.vetSystem.Entity.Mascota;
import com.vetSystem.Entity.Turno;
import com.vetSystem.Entity.Veterinario;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Exception.TurnoSuperpuestoException;
import com.vetSystem.Mapper.TurnoMapper;
import com.vetSystem.Repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private MascotaService mascotaService;

    @Mock
    private VeterinarioService veterinarioService;

    @Mock
    private TurnoMapper turnoMapper;

    @InjectMocks
    private TurnoService turnoService;

    private Mascota mascota;
    private Veterinario veterinario;
    private TurnoRequestDTO request;

    @BeforeEach
    void setUp() {
        mascota = new Mascota();
        mascota.setId(1L);
        mascota.setNombre("Firulais");

        veterinario = new Veterinario(1L, "Leandro", "Perez", "MV-4521", "Clinica General");

        request = new TurnoRequestDTO(
                LocalDate.now().plusDays(10),
                LocalTime.of(10, 30),
                "Control anual",
                1L,
                1L);
    }

    @Test
    void registrarTurno_cuandoNoHaySuperposicion_guardaUnaSolaVezEnEstadoPendiente() {
        // Arrange
        when(mascotaService.obtenerEntidad(1L)).thenReturn(mascota);
        when(veterinarioService.obtenerEntidad(1L)).thenReturn(veterinario);
        when(turnoRepository.existsByVeterinarioIdAndFechaAndHora(1L, request.getFecha(), request.getHora()))
                .thenReturn(false);
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnoMapper.toDTO(any(Turno.class))).thenReturn(new TurnoResponseDTO());

        // Act
        TurnoResponseDTO resultado = turnoService.registrarTurno(request);

        // Assert
        assertThat(resultado).isNotNull();

        ArgumentCaptor<Turno> captor = ArgumentCaptor.forClass(Turno.class);
        verify(turnoRepository, times(1)).save(captor.capture());

        Turno guardado = captor.getValue();
        assertThat(guardado.getEstado()).isEqualTo(EstadoTurno.PENDIENTE);
        assertThat(guardado.getMascota()).isSameAs(mascota);
        assertThat(guardado.getVeterinario()).isSameAs(veterinario);
        assertThat(guardado.getFecha()).isEqualTo(request.getFecha());
        assertThat(guardado.getHora()).isEqualTo(request.getHora());
    }

    @Test
    void registrarTurno_cuandoHaySuperposicion_lanzaTurnoSuperpuestoExceptionYNoGuarda() {
        // Arrange
        when(mascotaService.obtenerEntidad(1L)).thenReturn(mascota);
        when(veterinarioService.obtenerEntidad(1L)).thenReturn(veterinario);
        when(turnoRepository.existsByVeterinarioIdAndFechaAndHora(1L, request.getFecha(), request.getHora()))
                .thenReturn(true);

        // Act
        assertThrows(TurnoSuperpuestoException.class,
                () -> turnoService.registrarTurno(request));

        // Assert
        verify(turnoRepository, never()).save(any());
        verifyNoInteractions(turnoMapper);
    }

    @Test
    void registrarTurno_cuandoVeterinarioNoExiste_lanzaResourceNotFoundSinConsultarAgenda() {
        // Arrange
        when(mascotaService.obtenerEntidad(1L)).thenReturn(mascota);
        when(veterinarioService.obtenerEntidad(1L))
                .thenThrow(new ResourceNotFoundException("Veterinario", 1L));

        // Act
        assertThrows(ResourceNotFoundException.class,
                () -> turnoService.registrarTurno(request));

        // Assert
        verifyNoInteractions(turnoRepository);
        verifyNoInteractions(turnoMapper);
    }
}
