package com.patitas.auth.adapter.in.web;

import com.patitas.auth.adapter.in.web.dto.AuthResponse;
import com.patitas.auth.adapter.in.web.dto.LoginRequest;
import com.patitas.auth.adapter.in.web.dto.RegisterRequest;
import com.patitas.auth.domain.model.Usuario;
import com.patitas.auth.domain.port.in.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro y login de usuarios")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar un usuario")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = new Usuario(null, request.username(), request.password(), request.rol(), null);
        return ResponseEntity.ok(new AuthResponse(authService.register(usuario, request.matricula())));
    }

    @Operation(summary = "Iniciar sesión")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(new AuthResponse(authService.login(request.username(), request.password())));
    }
}
