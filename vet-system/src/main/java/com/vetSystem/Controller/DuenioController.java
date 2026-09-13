package com.vetSystem.Controller;

import com.vetSystem.DTO.DuenioDTO;
import com.vetSystem.DTO.MascotaDTO;
import com.vetSystem.Exception.DuplicateResourceException;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Service.DuenioService;
import com.vetSystem.Service.MascotaService;
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
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(duenioService.buscarPorId(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/mascotas")
    public ResponseEntity<?> listarMascotasDelDuenio(@PathVariable Long id) {
        try {
            List<MascotaDTO> mascotas = mascotaService.listarPorDuenio(id);
            return ResponseEntity.ok(mascotas);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearDuenio(@RequestBody DuenioDTO duenioDTO) {
        try {
            DuenioDTO nuevo = duenioService.registrarDuenio(duenioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDuenio(@PathVariable Long id,
                                              @RequestBody DuenioDTO duenioDTO) {
        try {
            return ResponseEntity.ok(duenioService.modificarDuenio(id, duenioDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDuenio(@PathVariable Long id) {
        try {
            duenioService.eliminarDuenio(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
