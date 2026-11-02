package com.patitas.turno.domain.exception;

import java.time.LocalDateTime;

public class TurnoSuperpuestoException extends RuntimeException {

    public TurnoSuperpuestoException(Long veterinarioId, LocalDateTime fechaHora) {
        super("El veterinario " + veterinarioId + " ya tiene un turno el " + fechaHora);
    }
}
