package com.patitas.dueno.domain.exception;

public class DuplicatedEmailException extends RuntimeException {

    public DuplicatedEmailException(String email) {
        super("Ya existe un dueño registrado con el email " + email);
    }
}
