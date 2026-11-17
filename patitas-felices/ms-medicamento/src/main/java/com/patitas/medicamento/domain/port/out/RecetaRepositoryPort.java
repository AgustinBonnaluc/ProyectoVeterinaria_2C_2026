package com.patitas.medicamento.domain.port.out;

import com.patitas.medicamento.domain.model.Receta;

import java.util.List;

public interface RecetaRepositoryPort {

    Receta save(Receta receta);

    List<Receta> findByTurnoId(Long turnoId);

    boolean existsByTurnoIdAndMedicamentoId(Long turnoId, Long medicamentoId);

    boolean existsByMedicamentoId(Long medicamentoId);
}
