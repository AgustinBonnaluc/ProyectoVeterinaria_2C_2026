package com.vetSystem.Exception;

public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public StockInsuficienteException(Long medicamentoId, String nombre) {
        super("El medicamento '" + nombre + "' (id " + medicamentoId
                + ") no tiene stock disponible. No se puede recetar.");
    }
}
