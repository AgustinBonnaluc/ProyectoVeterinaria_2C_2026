package com.patitas.turno.domain.port.in;

import com.patitas.turno.domain.model.Turno;

import java.util.List;

public interface TurnoService {

    List<Turno> findAll();

    Turno findById(Long id);

    List<Turno> findByVeterinarioId(Long veterinarioId);

    List<Turno> findByMascotaId(Long mascotaId);

    Turno save(Turno turno);

    Turno confirmar(Long id);

    Turno cancelar(Long id);
}
