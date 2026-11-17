package com.patitas.auth.domain.exception;

public class VeterinarioYaVinculadoException extends RuntimeException {

    public VeterinarioYaVinculadoException(String matricula) {
        super("El veterinario con matrícula " + matricula + " ya tiene una cuenta asociada");
    }
}
