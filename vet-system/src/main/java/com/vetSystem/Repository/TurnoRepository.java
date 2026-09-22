package com.vetSystem.Repository;

import com.vetSystem.Entity.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    // Regla de negocio: un veterinario no puede tener dos turnos a la misma hora el mismo día
    Optional<Turno> findByVeterinarioIdAndFechaAndHora(Long veterinarioId, LocalDate fecha, LocalTime hora);


    // Igual que la anterior, pero excluyendo un turno (para el PUT: no chocar consigo mismo)
    Optional<Turno> findByVeterinarioIdAndFechaAndHoraAndIdNot(Long veterinarioId, LocalDate fecha,
                                                               LocalTime hora, Long id);

    // Agenda de un veterinario en una fecha
    List<Turno> findByVeterinarioIdAndFechaOrderByHoraAsc(Long veterinarioId, LocalDate fecha);

    // Historial de turnos de una mascota
    List<Turno> findByMascotaIdOrderByFechaDescHoraDesc(Long mascotaId);
}
