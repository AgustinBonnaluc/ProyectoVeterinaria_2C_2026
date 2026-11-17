package com.patitas.medicamento.application;

import com.patitas.medicamento.domain.exception.DuplicatedNombreException;
import com.patitas.medicamento.domain.exception.MedicamentoConRecetasException;
import com.patitas.medicamento.domain.exception.MedicamentoNotFoundException;
import com.patitas.medicamento.domain.exception.MedicamentoYaRecetadoException;
import com.patitas.medicamento.domain.exception.StockInsuficienteException;
import com.patitas.medicamento.domain.exception.TurnoCanceladoException;
import com.patitas.medicamento.domain.exception.TurnoNotFoundException;
import com.patitas.medicamento.domain.model.Medicamento;
import com.patitas.medicamento.domain.model.Receta;
import com.patitas.medicamento.domain.port.in.MedicamentoService;
import com.patitas.medicamento.domain.port.out.MedicamentoRepositoryPort;
import com.patitas.medicamento.domain.port.out.RecetaRepositoryPort;
import com.patitas.medicamento.domain.port.out.TurnoServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoServiceImpl implements MedicamentoService {

    private static final String TURNO_CANCELADO = "CANCELADO";

    private final MedicamentoRepositoryPort medicamentoRepository;
    private final RecetaRepositoryPort recetaRepository;
    private final TurnoServicePort turnoService;

    @Override
    public List<Medicamento> findAll() {
        return medicamentoRepository.findAll();
    }

    @Override
    public Medicamento findById(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new MedicamentoNotFoundException(id));
    }

    @Override
    public Medicamento save(Medicamento medicamento) {
        if (medicamentoRepository.existsByNombreIgnoreCase(medicamento.getNombre())) {
            throw new DuplicatedNombreException(medicamento.getNombre());
        }
        medicamento.setId(null);
        return medicamentoRepository.save(medicamento);
    }

    @Override
    public Medicamento update(Long id, Medicamento datos) {
        Medicamento existente = findById(id);

        boolean cambioNombre = !existente.getNombre().equalsIgnoreCase(datos.getNombre());
        if (cambioNombre && medicamentoRepository.existsByNombreIgnoreCase(datos.getNombre())) {
            throw new DuplicatedNombreException(datos.getNombre());
        }

        existente.setNombre(datos.getNombre());
        existente.setPrincipioActivo(datos.getPrincipioActivo());
        existente.setStock(datos.getStock());
        existente.setPrecioUnitario(datos.getPrecioUnitario());
        return medicamentoRepository.save(existente);
    }

    @Override
    public void deleteById(Long id) {
        Medicamento medicamento = findById(id);
        if (recetaRepository.existsByMedicamentoId(id)) {
            throw new MedicamentoConRecetasException(medicamento.getNombre());
        }
        medicamentoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Receta recetar(Long medicamentoId, Long turnoId) {
        Medicamento medicamento = findById(medicamentoId);

        String estadoTurno = turnoService.buscarEstado(turnoId)
                .orElseThrow(() -> new TurnoNotFoundException(turnoId));
        if (TURNO_CANCELADO.equals(estadoTurno)) {
            throw new TurnoCanceladoException(turnoId);
        }
        if (recetaRepository.existsByTurnoIdAndMedicamentoId(turnoId, medicamentoId)) {
            throw new MedicamentoYaRecetadoException(medicamento.getNombre(), turnoId);
        }
        if (medicamento.getStock() <= 0) {
            throw new StockInsuficienteException(medicamento.getNombre());
        }

        medicamento.setStock(medicamento.getStock() - 1);
        medicamentoRepository.save(medicamento);
        return recetaRepository.save(new Receta(null, turnoId, medicamento, LocalDateTime.now()));
    }

    @Override
    public List<Receta> findRecetasByTurnoId(Long turnoId) {
        return recetaRepository.findByTurnoId(turnoId);
    }
}
