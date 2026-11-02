package com.patitas.turno.adapter.in.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequest {

    @NotNull(message = "La fecha y hora es obligatoria")
    @Future(message = "El turno debe ser en el futuro")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @NotNull(message = "El mascotaId es obligatorio")
    private Long mascotaId;

    @NotNull(message = "El veterinarioId es obligatorio")
    private Long veterinarioId;
}
