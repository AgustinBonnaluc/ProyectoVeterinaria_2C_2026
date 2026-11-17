package com.patitas.medicamento.adapter.out.persistence;

import com.patitas.medicamento.domain.model.Medicamento;
import com.patitas.medicamento.domain.port.out.MedicamentoRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentoJpaRepository extends JpaRepository<Medicamento, Long>, MedicamentoRepositoryPort {

    @Override
    boolean existsByNombreIgnoreCase(String nombre);
}
