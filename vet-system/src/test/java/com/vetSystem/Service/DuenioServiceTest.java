package com.vetSystem.Service;

import com.vetSystem.DTO.DuenioDTO;
import com.vetSystem.Entity.Duenio;
import com.vetSystem.Exception.DuplicateResourceException;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Mapper.DuenioMapper;
import com.vetSystem.Repository.DuenioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DuenioServiceTest {

    @Mock
    private DuenioRepository duenioRepository;

    @Mock
    private DuenioMapper duenioMapper;

    @InjectMocks
    private DuenioService duenioService;

    private Duenio duenio;
    private DuenioDTO duenioDTO;

    @BeforeEach
    void setUp() {
        duenio = new Duenio(1L, "Carlos", "Perez", "30111222", "1144445555", "carlos@mail.com", null);
        duenioDTO = new DuenioDTO(1L, "Carlos", "Perez", "30111222", "1144445555", "carlos@mail.com");
    }

    // ---------------------------------------------------------------
    // listarTodos
    // ---------------------------------------------------------------

    @Test
    void listarTodos_cuandoNoHayDuenios_retornaListaVacia() {
        // Arrange
        when(duenioRepository.findAll()).thenReturn(List.of());

        // Act
        List<DuenioDTO> resultado = duenioService.listarTodos();

        // Assert
        assertThat(resultado).isEmpty();
        verify(duenioRepository).findAll();
        verifyNoInteractions(duenioMapper);
    }

    @Test
    void listarTodos_cuandoHayDuenios_retornaListaDeDTOs() {
        // Arrange
        when(duenioRepository.findAll()).thenReturn(List.of(duenio));
        when(duenioMapper.toDTO(duenio)).thenReturn(duenioDTO);

        // Act
        List<DuenioDTO> resultado = duenioService.listarTodos();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Carlos");
        verify(duenioMapper, times(1)).toDTO(duenio);
    }

    // ---------------------------------------------------------------
    // buscarPorId
    // ---------------------------------------------------------------

    @Test
    void buscarPorId_cuandoExiste_retornaDTO() {
        // Arrange
        when(duenioRepository.findById(1L)).thenReturn(Optional.of(duenio));
        when(duenioMapper.toDTO(duenio)).thenReturn(duenioDTO);

        // Act
        DuenioDTO resultado = duenioService.buscarPorId(1L);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getCedula()).isEqualTo("30111222");
        verify(duenioRepository).findById(1L);
    }

    @Test
    void buscarPorId_cuandoNoExiste_lanzaResourceNotFoundException() {
        // Arrange
        when(duenioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> duenioService.buscarPorId(99L));

        // Assert
        assertThat(ex.getMessage()).contains("99");
        verifyNoInteractions(duenioMapper);
    }

    // ---------------------------------------------------------------
    // registrarDuenio
    // ---------------------------------------------------------------

    @Test
    void registrarDuenio_cuandoCedulaNueva_guardaYRetornaDTO() {
        // Arrange
        DuenioDTO entrada = new DuenioDTO(null, "Carlos", "Perez", "30111222", "1144445555", "carlos@mail.com");
        Duenio entidadSinId = new Duenio(null, "Carlos", "Perez", "30111222", "1144445555", "carlos@mail.com", null);

        when(duenioRepository.existsByCedula("30111222")).thenReturn(false);
        when(duenioMapper.toEntity(entrada)).thenReturn(entidadSinId);
        when(duenioRepository.save(entidadSinId)).thenReturn(duenio);
        when(duenioMapper.toDTO(duenio)).thenReturn(duenioDTO);

        // Act
        DuenioDTO resultado = duenioService.registrarDuenio(entrada);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(duenioRepository, times(1)).save(entidadSinId);
    }

    @Test
    void registrarDuenio_cuandoCedulaDuplicada_lanzaDuplicateResourceExceptionYNoGuarda() {
        // Arrange
        when(duenioRepository.existsByCedula("30111222")).thenReturn(true);

        // Act
        DuplicateResourceException ex = assertThrows(
                DuplicateResourceException.class,
                () -> duenioService.registrarDuenio(duenioDTO));

        // Assert
        assertThat(ex.getMessage()).contains("30111222");
        verify(duenioRepository, never()).save(any());
        verifyNoInteractions(duenioMapper);
    }
}