package com.patitas.turno.application;

import com.patitas.turno.domain.exception.EstadoTurnoInvalidoException;
import com.patitas.turno.domain.exception.MascotaNotFoundException;
import com.patitas.turno.domain.exception.TurnoNotFoundException;
import com.patitas.turno.domain.exception.TurnoSuperpuestoException;
import com.patitas.turno.domain.exception.VeterinarioNotFoundException;
import com.patitas.turno.domain.model.EstadoTurno;
import com.patitas.turno.domain.model.Turno;
import com.patitas.turno.domain.port.in.TurnoService;
import com.patitas.turno.domain.port.out.MascotaServicePort;
import com.patitas.turno.domain.port.out.TurnoRepositoryPort;
import com.patitas.turno.domain.port.out.VeterinarioServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepositoryPort turnoRepository;
    private final MascotaServicePort mascotaService;
    private final VeterinarioServicePort veterinarioService;

    @Override
    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }

    @Override
    public Turno findById(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException(id));
    }

    @Override
    public List<Turno> findByVeterinarioId(Long veterinarioId) {
        return turnoRepository.findByVeterinarioId(veterinarioId);
    }

    @Override
    public List<Turno> findByMascotaId(Long mascotaId) {
        return turnoRepository.findByMascotaId(mascotaId);
    }

    @Override
    public Turno save(Turno turno) {
        if (!mascotaService.existsById(turno.getMascotaId())) {
            throw new MascotaNotFoundException(turno.getMascotaId());
        }
        if (!veterinarioService.existsById(turno.getVeterinarioId())) {
            throw new VeterinarioNotFoundException(turno.getVeterinarioId());
        }

        boolean ocupado = turnoRepository.existsByVeterinarioIdAndFechaHoraAndEstadoNot(
                turno.getVeterinarioId(), turno.getFechaHora(), EstadoTurno.CANCELADO);
        if (ocupado) {
            throw new TurnoSuperpuestoException(turno.getVeterinarioId(), turno.getFechaHora());
        }

        turno.setId(null);
        turno.setEstado(EstadoTurno.PENDIENTE);
        return turnoRepository.save(turno);
    }

    @Override
    public Turno confirmar(Long id) {
        Turno turno = findById(id);
        if (turno.getEstado() != EstadoTurno.PENDIENTE) {
            throw new EstadoTurnoInvalidoException("Solo se puede confirmar un turno PENDIENTE (estado actual: " + turno.getEstado() + ")");
        }
        turno.setEstado(EstadoTurno.CONFIRMADO);
        return turnoRepository.save(turno);
    }

    @Override
    public Turno cancelar(Long id) {
        Turno turno = findById(id);
        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new EstadoTurnoInvalidoException("El turno " + id + " ya está cancelado");
        }
        turno.setEstado(EstadoTurno.CANCELADO);
        return turnoRepository.save(turno);
    }
}
