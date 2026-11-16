package com.patitas.auth.application.service;

import com.patitas.auth.domain.exception.CredencialesInvalidasException;
import com.patitas.auth.domain.exception.UsuarioYaExisteException;
import com.patitas.auth.domain.model.Usuario;
import com.patitas.auth.domain.port.in.AuthService;
import com.patitas.auth.domain.port.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String register(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new UsuarioYaExisteException(usuario.getUsername());
        }
        usuario.setId(null);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario guardado = usuarioRepository.save(usuario);
        return jwtService.generateToken(guardado.getUsername(), guardado.getRol());
    }

    @Override
    public String login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new CredencialesInvalidasException();
        }
        return jwtService.generateToken(usuario.getUsername(), usuario.getRol());
    }
}
