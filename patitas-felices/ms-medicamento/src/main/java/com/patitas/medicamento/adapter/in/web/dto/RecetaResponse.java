package com.patitas.medicamento.adapter.in.web.dto;

import java.time.LocalDateTime;

public record RecetaResponse(
        Long id,
        Long turnoId,
        Long medicamentoId,
        String medicamentoNombre,
        LocalDateTime fecha
) {
}
