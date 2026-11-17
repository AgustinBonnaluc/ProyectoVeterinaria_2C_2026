package com.patitas.auth.domain.exception;

public class VeterinarioNoEncontradoException extends RuntimeException {

    public VeterinarioNoEncontradoException(String matricula) {
        super("No existe un veterinario con la matrícula " + matricula + ". Primero hay que cargarlo en la sección Veterinarios");
    }
}
