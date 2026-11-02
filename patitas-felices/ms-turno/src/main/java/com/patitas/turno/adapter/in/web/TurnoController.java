package com.patitas.turno.adapter.in.web;

import com.patitas.turno.adapter.in.web.dto.TurnoMapper;
import com.patitas.turno.adapter.in.web.dto.TurnoRequest;
import com.patitas.turno.adapter.in.web.dto.TurnoResponse;
import com.patitas.turno.domain.port.in.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
@Tag(name = "Turnos", description = "Gestión de turnos")
public class TurnoController {

    private final TurnoService turnoService;

    @Operation(summary = "Listar todos los turnos")
    @GetMapping
    public ResponseEntity<List<TurnoResponse>> findAll() {
        return ResponseEntity.ok(turnoService.findAll().stream().map(TurnoMapper::toResponse).toList());
    }

    @Operation(summary = "Buscar un turno por ID")
    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(TurnoMapper.toResponse(turnoService.findById(id)));
    }

    @Operation(summary = "Listar turnos de un veterinario")
    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<TurnoResponse>> findByVeterinarioId(@PathVariable Long veterinarioId) {
        return ResponseEntity.ok(turnoService.findByVeterinarioId(veterinarioId).stream()
                .map(TurnoMapper::toResponse).toList());
    }

    @Operation(summary = "Listar turnos de una mascota")
    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<TurnoResponse>> findByMascotaId(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(turnoService.findByMascotaId(mascotaId).stream()
                .map(TurnoMapper::toResponse).toList());
    }

    @Operation(summary = "Crear un turno (valida mascota y veterinario contra sus microservicios)")
    @PostMapping
    public ResponseEntity<TurnoResponse> save(@Valid @RequestBody TurnoRequest request) {
        TurnoResponse creado = TurnoMapper.toResponse(turnoService.save(TurnoMapper.toEntity(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Confirmar un turno pendiente")
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<TurnoResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(TurnoMapper.toResponse(turnoService.confirmar(id)));
    }

    @Operation(summary = "Cancelar un turno")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<TurnoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(TurnoMapper.toResponse(turnoService.cancelar(id)));
    }
}
