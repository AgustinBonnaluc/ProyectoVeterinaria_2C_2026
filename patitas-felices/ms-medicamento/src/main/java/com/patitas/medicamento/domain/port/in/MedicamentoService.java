package com.patitas.medicamento.domain.port.in;

import com.patitas.medicamento.domain.model.Medicamento;
import com.patitas.medicamento.domain.model.Receta;

import java.util.List;

public interface MedicamentoService {

    List<Medicamento> findAll();

    Medicamento findById(Long id);

    Medicamento save(Medicamento medicamento);

    Medicamento update(Long id, Medicamento medicamento);

    void deleteById(Long id);

    Receta recetar(Long medicamentoId, Long turnoId);

    List<Receta> findRecetasByTurnoId(Long turnoId);
}
