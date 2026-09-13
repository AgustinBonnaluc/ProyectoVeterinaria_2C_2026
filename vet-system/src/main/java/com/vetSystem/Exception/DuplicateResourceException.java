package com.vetSystem.Exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String mensaje) {
        super(mensaje);
    }

    public DuplicateResourceException(String recurso, String campo, String valor) {
        super(recurso + " con " + campo + " '" + valor + "' ya existe");
    }
}
