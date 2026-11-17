package com.patitas.medicamento.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record RecetaRequest(
        @NotNull(message = "El turnoId es obligatorio") Long turnoId
) {
}
