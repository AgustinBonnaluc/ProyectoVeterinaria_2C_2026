package com.patitas.veterinario.adapter.out.persistence;

import com.patitas.veterinario.domain.model.Veterinario;
import com.patitas.veterinario.domain.port.out.VeterinarioRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarioJpaRepository extends JpaRepository<Veterinario, Long>, VeterinarioRepositoryPort {

    @Override
    List<Veterinario> findByEspecialidadIgnoreCase(String especialidad);

    @Override
    boolean existsByMatricula(String matricula);

    @Override
    Optional<Veterinario> findByMatricula(String matricula);
}
