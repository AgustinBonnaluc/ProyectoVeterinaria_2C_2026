package com.patitas.veterinario.domain.port.in;

import com.patitas.veterinario.domain.model.Veterinario;

import java.util.List;

public interface VeterinarioService {

    List<Veterinario> findAll();

    Veterinario findById(Long id);

    List<Veterinario> findByEspecialidad(String especialidad);

    Veterinario findByMatricula(String matricula);

    Veterinario save(Veterinario veterinario);

    Veterinario update(Long id, Veterinario veterinario);

    void deleteById(Long id);
}
