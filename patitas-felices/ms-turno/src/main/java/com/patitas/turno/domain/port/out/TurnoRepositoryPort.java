package com.patitas.turno.domain.port.out;

import com.patitas.turno.domain.model.EstadoTurno;
import com.patitas.turno.domain.model.Turno;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TurnoRepositoryPort {

    List<Turno> findAll();

    Optional<Turno> findById(Long id);

    List<Turno> findByVeterinarioId(Long veterinarioId);

    List<Turno> findByMascotaId(Long mascotaId);

    Turno save(Turno turno);

    boolean existsByVeterinarioIdAndFechaHoraAndEstadoNot(Long veterinarioId, LocalDateTime fechaHora, EstadoTurno estado);
}
