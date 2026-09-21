package com.vetSystem.Controller;

import com.vetSystem.DTO.DuenioDTO;
import com.vetSystem.DTO.MascotaDTO;
import com.vetSystem.Service.DuenioService;
import com.vetSystem.Service.MascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/duenio")
@RequiredArgsConstructor
public class DuenioController {

    private final DuenioService duenioService;
    private final MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<DuenioDTO>> buscarTodosLosDuenios() {
        return ResponseEntity.ok(duenioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DuenioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(duenioService.buscarPorId(id));
    }

    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<MascotaDTO>> listarMascotasDelDuenio(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.listarPorDuenio(id));
    }

    @PostMapping
    public ResponseEntity<DuenioDTO> crearDuenio(@Valid @RequestBody DuenioDTO duenioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(duenioService.registrarDuenio(duenioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DuenioDTO> actualizarDuenio(@PathVariable Long id,
                                                      @Valid @RequestBody DuenioDTO duenioDTO) {
        return ResponseEntity.ok(duenioService.modificarDuenio(id, duenioDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDuenio(@PathVariable Long id) {
        duenioService.eliminarDuenio(id);
        return ResponseEntity.noContent().build();
    }
}
