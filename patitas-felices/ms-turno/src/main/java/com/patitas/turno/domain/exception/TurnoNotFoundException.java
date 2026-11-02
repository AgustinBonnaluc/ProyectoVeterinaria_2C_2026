package com.patitas.turno.domain.exception;

public class TurnoNotFoundException extends RuntimeException {

    public TurnoNotFoundException(Long id) {
        super("Turno no encontrado: " + id);
    }
}
