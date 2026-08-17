package com.vetSystem.Repository;

import com.vetSystem.Entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MascotaRepository extends JpaRepository<Mascota,Long> {

}
