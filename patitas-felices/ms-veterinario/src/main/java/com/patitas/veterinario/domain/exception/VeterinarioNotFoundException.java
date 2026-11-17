package com.patitas.veterinario.domain.exception;

public class VeterinarioNotFoundException extends RuntimeException {

    public VeterinarioNotFoundException(Long id) {
        super("Veterinario no encontrado: " + id);
    }

    public VeterinarioNotFoundException(String matricula) {
        super("No existe un veterinario con la matrícula " + matricula);
    }
}
