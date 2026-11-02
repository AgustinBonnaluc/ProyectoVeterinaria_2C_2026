package com.patitas.turno.domain.exception;

public class EstadoTurnoInvalidoException extends RuntimeException {

    public EstadoTurnoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
