package com.patitas.medicamento.domain.exception;

public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String nombre) {
        super("El medicamento " + nombre + " no tiene stock disponible");
    }
}
