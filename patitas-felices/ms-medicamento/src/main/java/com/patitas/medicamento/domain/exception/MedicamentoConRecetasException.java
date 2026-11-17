package com.patitas.medicamento.domain.exception;

public class MedicamentoConRecetasException extends RuntimeException {

    public MedicamentoConRecetasException(String nombre) {
        super("No se puede eliminar " + nombre + " porque ya fue recetado en algún turno");
    }
}
