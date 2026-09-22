package com.vetSystem.Repository;

import com.vetSystem.Entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    boolean existsByNombre(String nombre);
}
