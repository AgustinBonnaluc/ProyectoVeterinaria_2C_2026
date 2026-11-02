package com.patitas.mascota.adapter.in.web.dto;

import com.patitas.mascota.domain.model.Mascota;

public final class MascotaMapper {

    private MascotaMapper() {
    }

    public static Mascota toEntity(MascotaRequest req) {
        return new Mascota(null, req.getNombre(), req.getEspecie(), req.getRaza(),
                req.getFechaNacimiento(), req.getDuenoId());
    }

    public static MascotaResponse toResponse(Mascota mascota) {
        return new MascotaResponse(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getFechaNacimiento(),
                mascota.getDuenoId()
        );
    }
}
