package com.vetSystem.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vetSystem.Entity.Duenio;

import java.util.Optional;

public interface DuenioRepository extends JpaRepository<Duenio,Long> {

    Optional<Duenio> findByNombre(String nombre);
    Optional<Duenio> findByEmail(String email);
    boolean existsByCedula(String cedula);

}
