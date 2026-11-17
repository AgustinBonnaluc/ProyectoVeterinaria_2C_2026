package com.patitas.medicamento.domain.exception;

public class TurnoNotFoundException extends RuntimeException {

    public TurnoNotFoundException(Long turnoId) {
        super("Turno no encontrado: " + turnoId);
    }
}
