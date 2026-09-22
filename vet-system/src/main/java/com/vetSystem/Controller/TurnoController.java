package com.vetSystem.Controller;

import com.vetSystem.DTO.TurnoRequestDTO;
import com.vetSystem.DTO.TurnoResponseDTO;
import com.vetSystem.Entity.EstadoTurno;
import com.vetSystem.Service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turno")
@RequiredArgsConstructor
@Tag(name = "Turnos", description = "Agenda de turnos: alta con control de superposición, consulta y cambio de estado")
public class TurnoController {

    private final TurnoService turnoService;

    @Operation(summary = "Listar turnos", description = "Devuelve todos los turnos registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<TurnoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(turnoService.listarTodos());
    }

    @Operation(summary = "Buscar turno por ID", description = "Devuelve el turno con el ID indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turno encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un turno con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> buscarPorId(
            @Parameter(description = "ID del turno", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(turnoService.buscarPorId(id));
    }

    @Operation(summary = "Consultar la agenda de un veterinario",
            description = "Devuelve los turnos de un veterinario en una fecha, ordenados por hora.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agenda del día"),
            @ApiResponse(responseCode = "404", description = "No existe un veterinario con ese ID")
    })
    @GetMapping("/agenda")
    public ResponseEntity<List<TurnoResponseDTO>> obtenerAgenda(
            @Parameter(description = "ID del veterinario", example = "1")
            @RequestParam Long veterinarioId,
            @Parameter(description = "Fecha a consultar (formato AAAA-MM-DD)", example = "2026-12-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(turnoService.obtenerAgenda(veterinarioId, fecha));
    }

    @Operation(summary = "Registrar un turno",
            description = "Crea un turno en estado PENDIENTE. Valida que la mascota y el veterinario existan "
                    + "y que el veterinario no tenga otro turno en la misma fecha y hora.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Turno creado en estado PENDIENTE"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (campos vacíos o fecha pasada)"),
            @ApiResponse(responseCode = "404", description = "La mascota o el veterinario no existen"),
            @ApiResponse(responseCode = "409", description = "El veterinario ya tiene un turno en ese horario")
    })
    @PostMapping
    public ResponseEntity<TurnoResponseDTO> registrarTurno(@Valid @RequestBody TurnoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.registrarTurno(request));
    }

    @Operation(summary = "Modificar un turno",
            description = "Cambia fecha, hora, motivo, mascota o veterinario, revalidando la superposición.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Turno actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "El turno, la mascota o el veterinario no existen"),
            @ApiResponse(responseCode = "409", description = "El veterinario ya tiene otro turno en ese horario")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> modificarTurno(
            @Parameter(description = "ID del turno", example = "1") @PathVariable Long id,
            @Valid @RequestBody TurnoRequestDTO request) {
        return ResponseEntity.ok(turnoService.modificarTurno(id, request));
    }

    @Operation(summary = "Cambiar el estado de un turno",
            description = "Actualización parcial: cambia sólo el estado y, opcionalmente, las observaciones.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "No existe un turno con ese ID")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TurnoResponseDTO> actualizarEstado(
            @Parameter(description = "ID del turno", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado del turno", example = "FINALIZADO")
            @RequestParam EstadoTurno estado,
            @Parameter(description = "Observaciones de la consulta (opcional)", example = "Sin novedades")
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(turnoService.actualizarEstado(id, estado, observaciones));
    }

    @Operation(summary = "Eliminar un turno")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Turno eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe un turno con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTurno(
            @Parameter(description = "ID del turno", example = "1") @PathVariable Long id) {
        turnoService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }
}
