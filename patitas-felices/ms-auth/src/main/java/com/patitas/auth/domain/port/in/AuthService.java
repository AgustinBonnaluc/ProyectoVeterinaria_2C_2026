package com.patitas.auth.domain.port.in;

import com.patitas.auth.domain.model.Usuario;

public interface AuthService {

    String register(Usuario usuario);

    String login(String username, String password);
}
