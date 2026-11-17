package com.patitas.auth.adapter.out.persistence;

import com.patitas.auth.domain.model.Usuario;
import com.patitas.auth.domain.port.out.UsuarioRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long>, UsuarioRepositoryPort {

    @Override
    Optional<Usuario> findByUsername(String username);

    @Override
    boolean existsByUsername(String username);

    @Override
    boolean existsByVeterinarioId(Long veterinarioId);
}
