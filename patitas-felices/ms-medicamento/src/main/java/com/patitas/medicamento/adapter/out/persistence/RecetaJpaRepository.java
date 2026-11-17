package com.patitas.medicamento.adapter.out.persistence;

import com.patitas.medicamento.domain.model.Receta;
import com.patitas.medicamento.domain.port.out.RecetaRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaJpaRepository extends JpaRepository<Receta, Long>, RecetaRepositoryPort {

    @Override
    List<Receta> findByTurnoId(Long turnoId);

    @Override
    boolean existsByTurnoIdAndMedicamentoId(Long turnoId, Long medicamentoId);

    @Override
    boolean existsByMedicamentoId(Long medicamentoId);
}
