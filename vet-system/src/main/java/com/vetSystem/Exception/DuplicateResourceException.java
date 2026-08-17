package com.vetSystem.Exception;

public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String recurso, String cedula) {
        super(recurso + " con CEDULA: " + cedula + " ya existe");

    }


}
