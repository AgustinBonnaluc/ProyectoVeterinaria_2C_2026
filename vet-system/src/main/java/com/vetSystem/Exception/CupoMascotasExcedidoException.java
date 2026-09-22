package com.vetSystem.Exception;

public class CupoMascotasExcedidoException extends RuntimeException {

    public CupoMascotasExcedidoException(Long duenioId, int limite, long actuales) {
        super("El dueño con id " + duenioId + " ya tiene " + actuales
                + " mascotas activas y el límite permitido es " + limite + ".");
    }
}