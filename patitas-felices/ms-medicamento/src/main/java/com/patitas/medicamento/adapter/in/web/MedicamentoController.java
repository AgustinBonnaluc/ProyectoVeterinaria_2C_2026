package com.patitas.medicamento.adapter.in.web;

import com.patitas.medicamento.adapter.in.web.dto.MedicamentoMapper;
import com.patitas.medicamento.adapter.in.web.dto.MedicamentoRequest;
import com.patitas.medicamento.adapter.in.web.dto.MedicamentoResponse;
import com.patitas.medicamento.adapter.in.web.dto.RecetaRequest;
import com.patitas.medicamento.adapter.in.web.dto.RecetaResponse;
import com.patitas.medicamento.domain.port.in.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
@Tag(name = "Medicamentos", description = "Stock de medicamentos y recetas por turno")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @Operation(summary = "Listar todos los medicamentos")
    @GetMapping
    public ResponseEntity<List<MedicamentoResponse>> findAll() {
        return ResponseEntity.ok(medicamentoService.findAll().stream().map(MedicamentoMapper::toResponse).toList());
    }

    @Operation(summary = "Buscar un medicamento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(MedicamentoMapper.toResponse(medicamentoService.findById(id)));
    }

    @Operation(summary = "Registrar un medicamento")
    @PostMapping
    public ResponseEntity<MedicamentoResponse> save(@Valid @RequestBody MedicamentoRequest request) {
        MedicamentoResponse creado = MedicamentoMapper.toResponse(medicamentoService.save(MedicamentoMapper.toEntity(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Modificar un medicamento")
    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody MedicamentoRequest request) {
        return ResponseEntity.ok(MedicamentoMapper.toResponse(medicamentoService.update(id, MedicamentoMapper.toEntity(request))));
    }

    @Operation(summary = "Eliminar un medicamento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        medicamentoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Recetar un medicamento en un turno (valida el turno contra ms-turno y descuenta stock)")
    @PostMapping("/{id}/recetas")
    public ResponseEntity<RecetaResponse> recetar(@PathVariable Long id, @Valid @RequestBody RecetaRequest request) {
        RecetaResponse receta = MedicamentoMapper.toResponse(medicamentoService.recetar(id, request.turnoId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(receta);
    }

    @Operation(summary = "Listar los medicamentos recetados en un turno")
    @GetMapping("/recetas/turno/{turnoId}")
    public ResponseEntity<List<RecetaResponse>> findRecetasByTurnoId(@PathVariable Long turnoId) {
        return ResponseEntity.ok(medicamentoService.findRecetasByTurnoId(turnoId).stream()
                .map(MedicamentoMapper::toResponse).toList());
    }
}
