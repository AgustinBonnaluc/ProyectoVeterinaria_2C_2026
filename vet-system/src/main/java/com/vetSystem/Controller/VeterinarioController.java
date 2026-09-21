package com.vetSystem.Controller;

import com.vetSystem.DTO.VeterinarioDTO;
import com.vetSystem.Service.VeterinarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinario")
@RequiredArgsConstructor
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    @GetMapping
    public ResponseEntity<List<VeterinarioDTO>> listarTodos() {
        return ResponseEntity.ok(veterinarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<VeterinarioDTO> registrarVeterinario(@Valid @RequestBody VeterinarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veterinarioService.registrarVeterinario(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> modificarVeterinario(@PathVariable Long id,
                                                               @Valid @RequestBody VeterinarioDTO dto) {
        return ResponseEntity.ok(veterinarioService.modificarVeterinario(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVeterinario(@PathVariable Long id) {
        veterinarioService.eliminarVeterinario(id);
        return ResponseEntity.noContent().build();
    }
}