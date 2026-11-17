package com.patitas.medicamento.domain.exception;

public class DuplicatedNombreException extends RuntimeException {

    public DuplicatedNombreException(String nombre) {
        super("Ya existe un medicamento con el nombre " + nombre);
    }
}
