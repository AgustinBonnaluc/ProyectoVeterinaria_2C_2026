package com.vetSystem.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenioDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede superar los 50 caracteres")
    private String apellido;

    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "\\d{7,8}", message = "La cédula debe tener 7 u 8 dígitos numéricos")
    private String cedula;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 8, max = 20, message = "El teléfono debe tener entre 8 y 20 caracteres")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
}