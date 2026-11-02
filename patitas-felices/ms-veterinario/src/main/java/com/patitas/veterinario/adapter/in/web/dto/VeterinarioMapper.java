package com.patitas.veterinario.adapter.in.web.dto;

import com.patitas.veterinario.domain.model.Veterinario;

public final class VeterinarioMapper {

    private VeterinarioMapper() {
    }

    public static Veterinario toEntity(VeterinarioRequest req) {
        return new Veterinario(null, req.getNombre(), req.getApellido(), req.getMatricula(),
                req.getEspecialidad(), req.getEmail());
    }

    public static VeterinarioResponse toResponse(Veterinario vet) {
        return new VeterinarioResponse(
                vet.getId(),
                vet.getNombre(),
                vet.getApellido(),
                vet.getMatricula(),
                vet.getEspecialidad(),
                vet.getEmail()
        );
    }
}
