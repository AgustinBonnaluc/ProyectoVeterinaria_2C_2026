package com.patitas.dueno.domain.exception;

public class DuenoNotFoundException extends RuntimeException {

    public DuenoNotFoundException(Long id) {
        super("No existe un dueño con id " + id);
    }
}
