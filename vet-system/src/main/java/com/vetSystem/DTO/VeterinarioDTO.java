package com.vetSystem.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de un veterinario de la clínica")
public class VeterinarioDTO {

    @Schema(description = "Identificador generado por el sistema", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del veterinario", example = "Leandro")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Apellido del veterinario", example = "Pérez")
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Schema(description = "Matrícula profesional con formato MV-0000", example = "MV-4521")
    @NotBlank(message = "La matrícula es obligatoria")
    @Pattern(regexp = "MV-\\d{4}", message = "La matrícula debe tener el formato MV-0000")
    private String matricula;

    @Schema(description = "Especialidad del veterinario", example = "Clínica general")
    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 60, message = "La especialidad no puede superar los 60 caracteres")
    private String especialidad;
}