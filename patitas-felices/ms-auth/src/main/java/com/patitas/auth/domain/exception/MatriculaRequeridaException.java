package com.patitas.auth.domain.exception;

public class MatriculaRequeridaException extends RuntimeException {

    public MatriculaRequeridaException() {
        super("Para crear una cuenta con rol VET hay que indicar la matrícula del veterinario");
    }
}
