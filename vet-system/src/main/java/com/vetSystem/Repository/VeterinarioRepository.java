package com.vetSystem.Repository;

import com.vetSystem.Entity.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarioRepository extends JpaRepository<Veterinario,Long> {
}
