package com.patitas.medicamento.domain.port.out;

import java.util.Optional;

public interface TurnoServicePort {

    Optional<String> buscarEstado(Long turnoId);
}
