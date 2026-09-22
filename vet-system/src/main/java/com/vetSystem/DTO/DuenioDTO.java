package com.vetSystem.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos de un dueño de mascotas")
public class DuenioDTO {

    @Schema(description = "Identificador generado por el sistema", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del dueño", example = "Lucía")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    private String nombre;

    @Schema(description = "Apellido del dueño", example = "Fernández")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede superar los 50 caracteres")
    private String apellido;

    @Schema(description = "Cédula de identidad, 7 u 8 dígitos sin puntos", example = "34567890")
    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "\\d{7,8}", message = "La cédula debe tener 7 u 8 dígitos numéricos")
    private String cedula;

    @Schema(description = "Teléfono de contacto", example = "1145678912")
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 8, max = 20, message = "El teléfono debe tener entre 8 y 20 caracteres")
    private String telefono;

    @Schema(description = "Correo electrónico de contacto", example = "lucia.fernandez@gmail.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
}