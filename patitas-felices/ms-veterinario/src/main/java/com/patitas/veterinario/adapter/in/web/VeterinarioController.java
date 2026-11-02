package com.patitas.veterinario.adapter.in.web;

import com.patitas.veterinario.adapter.in.web.dto.VeterinarioMapper;
import com.patitas.veterinario.adapter.in.web.dto.VeterinarioRequest;
import com.patitas.veterinario.adapter.in.web.dto.VeterinarioResponse;
import com.patitas.veterinario.domain.port.in.VeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
@Tag(name = "Veterinarios", description = "ABM y consulta de veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    @Operation(summary = "Listar todos los veterinarios")
    @GetMapping
    public ResponseEntity<List<VeterinarioResponse>> findAll() {
        return ResponseEntity.ok(veterinarioService.findAll().stream().map(VeterinarioMapper::toResponse).toList());
    }

    @Operation(summary = "Buscar un veterinario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(VeterinarioMapper.toResponse(veterinarioService.findById(id)));
    }

    @Operation(summary = "Listar veterinarios por especialidad")
    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<VeterinarioResponse>> findByEspecialidad(@PathVariable String especialidad) {
        return ResponseEntity.ok(veterinarioService.findByEspecialidad(especialidad).stream()
                .map(VeterinarioMapper::toResponse).toList());
    }

    @Operation(summary = "Registrar un veterinario")
    @PostMapping
    public ResponseEntity<VeterinarioResponse> save(@Valid @RequestBody VeterinarioRequest request) {
        VeterinarioResponse creado = VeterinarioMapper.toResponse(veterinarioService.save(VeterinarioMapper.toEntity(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Modificar un veterinario")
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody VeterinarioRequest request) {
        return ResponseEntity.ok(VeterinarioMapper.toResponse(veterinarioService.update(id, VeterinarioMapper.toEntity(request))));
    }

    @Operation(summary = "Eliminar un veterinario")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        veterinarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
