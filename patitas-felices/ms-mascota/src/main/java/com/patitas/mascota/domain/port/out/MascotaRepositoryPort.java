package com.patitas.mascota.domain.port.out;

import com.patitas.mascota.domain.model.Mascota;

import java.util.List;
import java.util.Optional;

public interface MascotaRepositoryPort {

    List<Mascota> findAll();

    Optional<Mascota> findById(Long id);

    List<Mascota> findByDuenoId(Long duenoId);

    Mascota save(Mascota mascota);

    void deleteById(Long id);

    boolean existsById(Long id);
}
