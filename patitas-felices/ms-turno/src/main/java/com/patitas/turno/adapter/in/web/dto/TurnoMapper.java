package com.patitas.turno.adapter.in.web.dto;

import com.patitas.turno.domain.model.Turno;

public final class TurnoMapper {

    private TurnoMapper() {
    }

    public static Turno toEntity(TurnoRequest req) {
        return new Turno(null, req.getFechaHora(), req.getMascotaId(), req.getVeterinarioId(),
                req.getMotivo(), null);
    }

    public static TurnoResponse toResponse(Turno turno) {
        return new TurnoResponse(
                turno.getId(),
                turno.getFechaHora(),
                turno.getMotivo(),
                turno.getMascotaId(),
                turno.getVeterinarioId(),
                turno.getEstado().name()
        );
    }
}
