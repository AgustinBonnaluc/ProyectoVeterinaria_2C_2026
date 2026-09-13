package com.vetSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequestDTO {

    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;

    private Long mascotaId;
    private Long veterinarioId;

    // NO tiene id     → lo genera la base de datos
    // NO tiene estado → siempre arranca en PENDIENTE, el cliente no puede elegirlo
}
