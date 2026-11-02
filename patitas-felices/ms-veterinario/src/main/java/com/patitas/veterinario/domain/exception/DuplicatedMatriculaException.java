package com.patitas.veterinario.domain.exception;

public class DuplicatedMatriculaException extends RuntimeException {

    public DuplicatedMatriculaException(String matricula) {
        super("Ya existe un veterinario con la matrícula " + matricula);
    }
}
