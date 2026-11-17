package com.patitas.auth.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "El username es obligatorio") String username,
        @NotBlank(message = "La contraseña es obligatoria") String password,
        @NotBlank(message = "El rol es obligatorio")
        @Pattern(regexp = "ADMIN|VET|RECEPCIONISTA", message = "El rol debe ser ADMIN, VET o RECEPCIONISTA") String rol,
        String matricula
) {
}
