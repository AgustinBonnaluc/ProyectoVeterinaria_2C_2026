package com.vetSystem.Controller;

import com.vetSystem.DTO.DuenioDTO;
import com.vetSystem.DTO.MascotaDTO;
import com.vetSystem.Service.DuenioService;
import com.vetSystem.Service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/duenio")
@RequiredArgsConstructor
@Tag(name = "Dueños", description = "Alta, baja, modificación y consulta de los dueños de las mascotas")
public class DuenioController {

    private final DuenioService duenioService;
    private final MascotaService mascotaService;

    @Operation(summary = "Listar dueños",
            description = "Devuelve todos los dueños registrados. Si no hay ninguno, devuelve una lista vacía.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<DuenioDTO>> buscarTodosLosDuenios() {
        return ResponseEntity.ok(duenioService.listarTodos());
    }

    @Operation(summary = "Buscar dueño por ID",
            description = "Devuelve el dueño con el ID indicado. Falla con 404 si no existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dueño encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un dueño con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DuenioDTO> buscarPorId(
            @Parameter(description = "ID del dueño", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(duenioService.buscarPorId(id));
    }

    @Operation(summary = "Listar las mascotas de un dueño",
            description = "Devuelve las mascotas asociadas al dueño. Falla con 404 si el dueño no existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de mascotas del dueño"),
            @ApiResponse(responseCode = "404", description = "No existe un dueño con ese ID")
    })
    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<MascotaDTO>> listarMascotasDelDuenio(
            @Parameter(description = "ID del dueño", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.listarPorDuenio(id));
    }

    @Operation(summary = "Registrar un dueño",
            description = "Crea un dueño nuevo. Falla con 400 si los datos no pasan la validación "
                    + "y con 409 si la cédula ya está registrada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dueño creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (campos vacíos, email o cédula con formato incorrecto)"),
            @ApiResponse(responseCode = "409", description = "Ya existe un dueño con esa cédula")
    })
    @PostMapping
    public ResponseEntity<DuenioDTO> crearDuenio(@Valid @RequestBody DuenioDTO duenioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(duenioService.registrarDuenio(duenioDTO));
    }

    @Operation(summary = "Modificar un dueño",
            description = "Actualiza nombre, apellido, teléfono y email. La cédula no se modifica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dueño actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "No existe un dueño con ese ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DuenioDTO> actualizarDuenio(
            @Parameter(description = "ID del dueño", example = "1") @PathVariable Long id,
            @Valid @RequestBody DuenioDTO duenioDTO) {
        return ResponseEntity.ok(duenioService.modificarDuenio(id, duenioDTO));
    }

    @Operation(summary = "Eliminar un dueño",
            description = "Elimina el dueño y, en cascada, sus mascotas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dueño eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe un dueño con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDuenio(
            @Parameter(description = "ID del dueño", example = "1") @PathVariable Long id) {
        duenioService.eliminarDuenio(id);
        return ResponseEntity.noContent().build();
    }
}
