package com.patitas.dueno.adapter.in.web;

import com.patitas.dueno.adapter.in.web.dto.DuenoMapper;
import com.patitas.dueno.adapter.in.web.dto.DuenoRequest;
import com.patitas.dueno.adapter.in.web.dto.DuenoResponse;
import com.patitas.dueno.domain.port.in.DuenoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/duenos")
@RequiredArgsConstructor
@Tag(name = "Dueños", description = "ABM y consulta de dueños")
public class DuenoController {

    private final DuenoService duenoService;

    @Operation(summary = "Listar todos los dueños")
    @GetMapping
    public ResponseEntity<List<DuenoResponse>> findAll() {
        List<DuenoResponse> duenos = duenoService.findAll()
                .stream()
                .map(DuenoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(duenos);
    }

    @Operation(summary = "Buscar un dueño por ID")
    @GetMapping("/{id}")
    public ResponseEntity<DuenoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(DuenoMapper.toResponse(duenoService.findById(id)));
    }

    // Llega el POST y se lo paso al service
    @Operation(summary = "Registrar un dueño")
    @PostMapping
    public ResponseEntity<DuenoResponse> save(@Valid @RequestBody DuenoRequest request) {
        DuenoResponse creado = DuenoMapper.toResponse(duenoService.save(DuenoMapper.toEntity(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Modificar un dueño")
    @PutMapping("/{id}")
    public ResponseEntity<DuenoResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody DuenoRequest request) {
        return ResponseEntity.ok(DuenoMapper.toResponse(duenoService.update(id, DuenoMapper.toEntity(request))));
    }

    @Operation(summary = "Eliminar un dueño")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        duenoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
