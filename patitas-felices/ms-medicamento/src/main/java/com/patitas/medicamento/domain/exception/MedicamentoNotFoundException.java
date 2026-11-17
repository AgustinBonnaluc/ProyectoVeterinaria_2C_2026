package com.patitas.medicamento.domain.exception;

public class MedicamentoNotFoundException extends RuntimeException {

    public MedicamentoNotFoundException(Long id) {
        super("Medicamento no encontrado: " + id);
    }
}
