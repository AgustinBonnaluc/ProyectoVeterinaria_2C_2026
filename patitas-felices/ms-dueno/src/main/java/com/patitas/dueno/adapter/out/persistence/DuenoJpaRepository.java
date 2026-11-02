package com.patitas.dueno.adapter.out.persistence;

import com.patitas.dueno.domain.model.Dueno;
import com.patitas.dueno.domain.port.out.DuenoRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DuenoJpaRepository extends JpaRepository<Dueno, Long>, DuenoRepositoryPort {

    // Spring Data arma la consulta con el nombre del método
    @Override
    boolean existsByEmail(String email);
}
