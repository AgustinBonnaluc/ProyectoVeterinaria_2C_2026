package com.vetSystem.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos para registrar o modificar un turno. El estado no se envía: siempre arranca en PENDIENTE.")
public class TurnoRequestDTO {

    @Schema(description = "Fecha del turno (hoy o posterior)", example = "2026-12-15")
    @NotNull(message = "La fecha del turno es obligatoria")
    @FutureOrPresent(message = "No se puede agendar un turno en una fecha pasada")
    private LocalDate fecha;

    @Schema(description = "Hora del turno", example = "10:30:00", type = "string")
    @NotNull(message = "La hora del turno es obligatoria")
    private LocalTime hora;

    @Schema(description = "Motivo de la consulta", example = "Control anual y vacuna antirrábica")
    @NotBlank(message = "El motivo de la consulta es obligatorio")
    @Size(max = 200, message = "El motivo no puede superar los 200 caracteres")
    private String motivo;

    @Schema(description = "ID de la mascota que asiste al turno", example = "1")
    @NotNull(message = "El id de la mascota es obligatorio")
    @Positive(message = "El id de la mascota debe ser un número positivo")
    private Long mascotaId;

    @Schema(description = "ID del veterinario que atiende", example = "1")
    @NotNull(message = "El id del veterinario es obligatorio")
    @Positive(message = "El id del veterinario debe ser un número positivo")
    private Long veterinarioId;
}