package com.patitas.mascota.adapter.in.web;

import com.patitas.mascota.adapter.in.web.dto.MascotaMapper;
import com.patitas.mascota.adapter.in.web.dto.MascotaRequest;
import com.patitas.mascota.adapter.in.web.dto.MascotaResponse;
import com.patitas.mascota.domain.port.in.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "ABM y consulta de mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    @Operation(summary = "Listar todas las mascotas")
    @GetMapping
    public ResponseEntity<List<MascotaResponse>> findAll() {
        return ResponseEntity.ok(mascotaService.findAll().stream().map(MascotaMapper::toResponse).toList());
    }

    @Operation(summary = "Buscar una mascota por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(MascotaMapper.toResponse(mascotaService.findById(id)));
    }

    @Operation(summary = "Listar las mascotas de un dueño")
    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<MascotaResponse>> findByDuenoId(@PathVariable Long duenoId) {
        return ResponseEntity.ok(mascotaService.findByDuenoId(duenoId).stream().map(MascotaMapper::toResponse).toList());
    }

    @Operation(summary = "Registrar una mascota")
    @PostMapping
    public ResponseEntity<MascotaResponse> save(@Valid @RequestBody MascotaRequest request) {
        MascotaResponse creada = MascotaMapper.toResponse(mascotaService.save(MascotaMapper.toEntity(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @Operation(summary = "Modificar una mascota")
    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody MascotaRequest request) {
        return ResponseEntity.ok(MascotaMapper.toResponse(mascotaService.update(id, MascotaMapper.toEntity(request))));
    }

    @Operation(summary = "Eliminar una mascota")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        mascotaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
