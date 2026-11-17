package com.patitas.medicamento.adapter.in.web.dto;

import com.patitas.medicamento.domain.model.Medicamento;
import com.patitas.medicamento.domain.model.Receta;

public final class MedicamentoMapper {

    private MedicamentoMapper() {
    }

    public static Medicamento toEntity(MedicamentoRequest req) {
        return new Medicamento(null, req.getNombre(), req.getPrincipioActivo(), req.getStock(), req.getPrecioUnitario());
    }

    public static MedicamentoResponse toResponse(Medicamento medicamento) {
        return new MedicamentoResponse(
                medicamento.getId(),
                medicamento.getNombre(),
                medicamento.getPrincipioActivo(),
                medicamento.getStock(),
                medicamento.getPrecioUnitario()
        );
    }

    public static RecetaResponse toResponse(Receta receta) {
        return new RecetaResponse(
                receta.getId(),
                receta.getTurnoId(),
                receta.getMedicamento().getId(),
                receta.getMedicamento().getNombre(),
                receta.getFecha()
        );
    }
}
