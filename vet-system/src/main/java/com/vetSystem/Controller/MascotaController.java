package com.vetSystem.Controller;

import com.vetSystem.DTO.MascotaDTO;
import com.vetSystem.Service.MascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascota")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<MascotaDTO>> listarTodas() {
        return ResponseEntity.ok(mascotaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MascotaDTO> registrarMascota(@Valid @RequestBody MascotaDTO mascotaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.guardarMascota(mascotaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaDTO> modificarMascota(@PathVariable Long id,
                                                       @Valid @RequestBody MascotaDTO mascotaDTO) {
        return ResponseEntity.ok(mascotaService.modificarMascota(id, mascotaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
        return ResponseEntity.noContent().build();
    }
}
