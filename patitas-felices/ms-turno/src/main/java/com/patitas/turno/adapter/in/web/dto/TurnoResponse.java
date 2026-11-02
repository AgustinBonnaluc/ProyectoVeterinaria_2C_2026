package com.patitas.turno.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponse {

    private Long id;
    private LocalDateTime fechaHora;
    private String motivo;
    private Long mascotaId;
    private Long veterinarioId;
    private String estado;
}
