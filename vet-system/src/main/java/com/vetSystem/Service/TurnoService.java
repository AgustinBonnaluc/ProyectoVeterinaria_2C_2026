package com.vetSystem.Service;

import com.vetSystem.DTO.MedicamentoResponseDTO;
import com.vetSystem.DTO.TurnoRequestDTO;
import com.vetSystem.DTO.TurnoResponseDTO;
import com.vetSystem.Entity.*;
import com.vetSystem.Exception.DuplicateResourceException;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Exception.TurnoSuperpuestoException;
import com.vetSystem.Mapper.MedicamentoMapper;
import com.vetSystem.Mapper.TurnoMapper;
import com.vetSystem.Repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final TurnoMapper turnoMapper;

    private final MedicamentoService medicamentoService;
    private final MedicamentoMapper medicamentoMapper;

    @Transactional(readOnly = true)
    public Turno obtenerEntidad(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno", id));
    }

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> listarTodos() {
        return turnoRepository.findAll()
                .stream()
                .map(turnoMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TurnoResponseDTO buscarPorId(Long id) {
        return turnoMapper.toDTO(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> obtenerAgenda(Long veterinarioId, LocalDate fecha) {
        veterinarioService.obtenerEntidad(veterinarioId); // 404 si el veterinario no existe
        return turnoRepository.findByVeterinarioIdAndFechaOrderByHoraAsc(veterinarioId, fecha)
                .stream()
                .map(turnoMapper::toDTO)
                .toList();
    }

    @Transactional
    public TurnoResponseDTO registrarTurno(TurnoRequestDTO request) {
        // 1. La mascota tiene que existir  → 404 si no
        Mascota mascota = mascotaService.obtenerEntidad(request.getMascotaId());

        // 2. El veterinario tiene que existir → 404 si no
        Veterinario veterinario = veterinarioService.obtenerEntidad(request.getVeterinarioId());

        // 3. REGLA DE NEGOCIO: no puede haber superposición → 409 si la hay
        if (turnoRepository.existsByVeterinarioIdAndFechaAndHora(
                request.getVeterinarioId(), request.getFecha(), request.getHora())) {
            throw new TurnoSuperpuestoException(
                    request.getVeterinarioId(), request.getFecha(), request.getHora());
        }

        // 4. Recién ahora construimos y persistimos
        Turno turno = new Turno();
        turno.setFecha(request.getFecha());
        turno.setHora(request.getHora());
        turno.setMotivo(request.getMotivo());
        turno.setEstado(EstadoTurno.PENDIENTE); // el estado inicial lo decide el servidor
        turno.setMascota(mascota);
        turno.setVeterinario(veterinario);

        return turnoMapper.toDTO(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoResponseDTO modificarTurno(Long id, TurnoRequestDTO request) {
        Turno turno = obtenerEntidad(id);

        Mascota mascota = mascotaService.obtenerEntidad(request.getMascotaId());
        Veterinario veterinario = veterinarioService.obtenerEntidad(request.getVeterinarioId());

        // misma validación, pero sin contarse a sí mismo
        if (turnoRepository.existsByVeterinarioIdAndFechaAndHoraAndIdNot(
                request.getVeterinarioId(), request.getFecha(), request.getHora(), id)) {
            throw new TurnoSuperpuestoException(
                    request.getVeterinarioId(), request.getFecha(), request.getHora());
        }

        turno.setFecha(request.getFecha());
        turno.setHora(request.getHora());
        turno.setMotivo(request.getMotivo());
        turno.setMascota(mascota);
        turno.setVeterinario(veterinario);

        return turnoMapper.toDTO(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoResponseDTO actualizarEstado(Long id, EstadoTurno nuevoEstado, String observaciones) {
        Turno turno = obtenerEntidad(id);
        turno.setEstado(nuevoEstado);
        if (observaciones != null && !observaciones.isBlank()) {
            turno.setObservaciones(observaciones);
        }
        return turnoMapper.toDTO(turnoRepository.save(turno));
    }

    @Transactional
    public void eliminarTurno(Long id) {
        turnoRepository.delete(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarMedicamentos(Long turnoId) {
        Turno turno = obtenerEntidad(turnoId);
        return turno.getMedicamentos()
                .stream()
                .map(medicamentoMapper::toDTO)
                .toList();
    }

    @Transactional
    public List<MedicamentoResponseDTO> recetarMedicamento(Long turnoId, Long medicamentoId) {
        // comprueba si existe el turno
        Turno turno = obtenerEntidad(turnoId);

        // comprueba si existe el medicamento
        Medicamento medicamento = medicamentoService.obtenerEntidad(medicamentoId);

        // si el medicamento ya se recetó no se puede recetar devuelta en eel turno
        if (turno.getMedicamentos().contains(medicamento)) {
            throw new DuplicateResourceException(
                    "El medicamento '" + medicamento.getNombre()
                            + "' ya está recetado en el turno " + turnoId);
        }

        // valida stock y/o descuenta stock (falla si stock es menor o igual a 0)
        medicamentoService.descontarUnidad(medicamento);
        // si no falla, agrega el medicamento al turno
        turno.getMedicamentos().add(medicamento);
        turnoRepository.save(turno);

        return turno.getMedicamentos().stream().map(medicamentoMapper::toDTO).toList();
    }


}