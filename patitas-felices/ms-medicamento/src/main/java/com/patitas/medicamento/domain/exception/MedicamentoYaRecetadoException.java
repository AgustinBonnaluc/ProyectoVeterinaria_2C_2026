package com.patitas.medicamento.domain.exception;

public class MedicamentoYaRecetadoException extends RuntimeException {

    public MedicamentoYaRecetadoException(String nombre, Long turnoId) {
        super("El medicamento " + nombre + " ya fue recetado en el turno " + turnoId);
    }
}
