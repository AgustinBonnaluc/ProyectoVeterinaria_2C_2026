package com.patitas.auth.application.service;

import com.patitas.auth.domain.exception.CredencialesInvalidasException;
import com.patitas.auth.domain.exception.MatriculaRequeridaException;
import com.patitas.auth.domain.exception.UsuarioYaExisteException;
import com.patitas.auth.domain.exception.VeterinarioNoEncontradoException;
import com.patitas.auth.domain.exception.VeterinarioYaVinculadoException;
import com.patitas.auth.domain.model.Usuario;
import com.patitas.auth.domain.port.in.AuthService;
import com.patitas.auth.domain.port.out.UsuarioRepositoryPort;
import com.patitas.auth.domain.port.out.VeterinarioServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String ROL_VETERINARIO = "VET";

    private final UsuarioRepositoryPort usuarioRepository;
    private final VeterinarioServicePort veterinarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public String register(Usuario usuario, String matricula) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new UsuarioYaExisteException(usuario.getUsername());
        }

        usuario.setVeterinarioId(null);
        if (ROL_VETERINARIO.equals(usuario.getRol())) {
            usuario.setVeterinarioId(vincularVeterinario(matricula));
        }

        usuario.setId(null);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario guardado = usuarioRepository.save(usuario);
        return jwtService.generateToken(guardado.getUsername(), guardado.getRol(), guardado.getVeterinarioId());
    }

    @Override
    public String login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new CredencialesInvalidasException();
        }
        return jwtService.generateToken(usuario.getUsername(), usuario.getRol(), usuario.getVeterinarioId());
    }

    private Long vincularVeterinario(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            throw new MatriculaRequeridaException();
        }
        Long veterinarioId = veterinarioService.buscarIdPorMatricula(matricula.trim())
                .orElseThrow(() -> new VeterinarioNoEncontradoException(matricula.trim()));
        if (usuarioRepository.existsByVeterinarioId(veterinarioId)) {
            throw new VeterinarioYaVinculadoException(matricula.trim());
        }
        return veterinarioId;
    }
}
