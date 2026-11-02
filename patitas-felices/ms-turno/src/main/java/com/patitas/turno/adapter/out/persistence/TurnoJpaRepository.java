package com.patitas.turno.adapter.out.persistence;

import com.patitas.turno.domain.model.EstadoTurno;
import com.patitas.turno.domain.model.Turno;
import com.patitas.turno.domain.port.out.TurnoRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurnoJpaRepository extends JpaRepository<Turno, Long>, TurnoRepositoryPort {

    @Override
    List<Turno> findByVeterinarioId(Long veterinarioId);

    @Override
    List<Turno> findByMascotaId(Long mascotaId);

    @Override
    boolean existsByVeterinarioIdAndFechaHoraAndEstadoNot(Long veterinarioId, LocalDateTime fechaHora, EstadoTurno estado);
}
