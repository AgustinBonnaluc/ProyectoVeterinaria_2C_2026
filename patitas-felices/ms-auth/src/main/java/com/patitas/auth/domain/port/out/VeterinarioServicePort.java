package com.patitas.auth.domain.port.out;

import java.util.Optional;

public interface VeterinarioServicePort {

    Optional<Long> buscarIdPorMatricula(String matricula);
}
