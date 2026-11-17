package com.patitas.medicamento.domain.exception;

public class TurnoCanceladoException extends RuntimeException {

    public TurnoCanceladoException(Long turnoId) {
        super("No se puede recetar en el turno " + turnoId + " porque está cancelado");
    }
}
