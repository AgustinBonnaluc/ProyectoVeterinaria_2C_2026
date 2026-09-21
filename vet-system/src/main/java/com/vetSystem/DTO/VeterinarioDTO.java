package com.vetSystem.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "La matrícula es obligatoria")
    @Pattern(regexp = "MV-\\d{4}", message = "La matrícula debe tener el formato MV-0000")
    private String matricula;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 60, message = "La especialidad no puede superar los 60 caracteres")
    private String especialidad;
}