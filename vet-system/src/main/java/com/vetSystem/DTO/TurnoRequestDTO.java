package com.vetSystem.DTO;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequestDTO {

    @NotNull(message = "La fecha del turno es obligatoria")
    @FutureOrPresent(message = "No se puede agendar un turno en una fecha pasada")
    private LocalDate fecha;

    @NotNull(message = "La hora del turno es obligatoria")
    private LocalTime hora;

    @NotBlank(message = "El motivo de la consulta es obligatorio")
    @Size(max = 200, message = "El motivo no puede superar los 200 caracteres")
    private String motivo;

    @NotNull(message = "El id de la mascota es obligatorio")
    @Positive(message = "El id de la mascota debe ser un número positivo")
    private Long mascotaId;

    @NotNull(message = "El id del veterinario es obligatorio")
    @Positive(message = "El id del veterinario debe ser un número positivo")
    private Long veterinarioId;
}
