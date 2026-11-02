package com.patitas.veterinario.domain.port.out;

import com.patitas.veterinario.domain.model.Veterinario;

import java.util.List;
import java.util.Optional;

public interface VeterinarioRepositoryPort {

    List<Veterinario> findAll();

    Optional<Veterinario> findById(Long id);

    List<Veterinario> findByEspecialidadIgnoreCase(String especialidad);

    Veterinario save(Veterinario veterinario);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByMatricula(String matricula);
}
