package com.patitas.auth.domain.port.out;

import com.patitas.auth.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    Usuario save(Usuario usuario);
}
