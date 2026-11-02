package com.patitas.mascota.adapter.out.persistence;

import com.patitas.mascota.domain.model.Mascota;
import com.patitas.mascota.domain.port.out.MascotaRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaJpaRepository extends JpaRepository<Mascota, Long>, MascotaRepositoryPort {

    @Override
    List<Mascota> findByDuenoId(Long duenoId);
}
