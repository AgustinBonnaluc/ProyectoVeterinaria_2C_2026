package com.vetSystem.Repository;

import com.vetSystem.Entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota,Long> {

    List<Mascota> findByDuenioId(Long duenioId);
    boolean existsByNombreAndDuenioId(String nombre, Long duenioId);
}
