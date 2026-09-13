package com.vetSystem.Repository;

import com.vetSystem.Entity.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    boolean existsByMatricula(String matricula);

    Optional<Veterinario> findByMatricula(String matricula);
}
