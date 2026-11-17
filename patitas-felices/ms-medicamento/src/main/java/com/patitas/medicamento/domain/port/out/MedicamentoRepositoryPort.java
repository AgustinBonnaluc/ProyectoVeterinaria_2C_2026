package com.patitas.medicamento.domain.port.out;

import com.patitas.medicamento.domain.model.Medicamento;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepositoryPort {

    List<Medicamento> findAll();

    Optional<Medicamento> findById(Long id);

    Medicamento save(Medicamento medicamento);

    void deleteById(Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
