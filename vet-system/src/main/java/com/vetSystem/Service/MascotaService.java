package com.vetSystem.Service;

import com.vetSystem.DTO.MascotaDTO;
import com.vetSystem.Entity.Duenio;
import com.vetSystem.Entity.Mascota;
import com.vetSystem.Exception.DuplicateResourceException;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Mapper.MascotaMapper;
import com.vetSystem.Repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final DuenioService duenioService;
    private final MascotaMapper mascotaMapper;

    @Transactional(readOnly = true)
    public Mascota obtenerEntidad(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", id));
    }

    @Transactional(readOnly = true)
    public List<MascotaDTO> listarTodos() {
        return mascotaRepository.findAll()
                .stream()
                .map(mascotaMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MascotaDTO buscarPorId(Long id) {
        return mascotaMapper.toDTO(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<MascotaDTO> listarPorDuenio(Long duenioId) {
        duenioService.obtenerEntidad(duenioId); // 404 si el dueño no existe
        return mascotaRepository.findByDuenioId(duenioId)
                .stream()
                .map(mascotaMapper::toDTO)
                .toList();
    }

    @Transactional
    public MascotaDTO guardarMascota(MascotaDTO dto) {
        Duenio duenio = duenioService.obtenerEntidad(dto.getDuenioId());

        if (mascotaRepository.existsByNombreAndDuenioId(dto.getNombre(), dto.getDuenioId())) {
            throw new DuplicateResourceException(
                    "Mascota", "nombre", dto.getNombre());
        }

        Mascota mascota = mascotaMapper.toEntity(dto);
        mascota.setId(null);
        mascota.setDuenio(duenio); // el mapper lo ignoró: lo resuelve el Service
        return mascotaMapper.toDTO(mascotaRepository.save(mascota));
    }

    @Transactional
    public MascotaDTO modificarMascota(Long id, MascotaDTO dto) {
        Mascota mascota = obtenerEntidad(id);

        mascota.setNombre(dto.getNombre());
        mascota.setEspecie(dto.getEspecie());
        mascota.setRaza(dto.getRaza());
        mascota.setFechaNacimiento(dto.getFechaNacimiento());

        // Cambio de dueño opcional
        if (dto.getDuenioId() != null && !dto.getDuenioId().equals(mascota.getDuenio().getId())) {
            mascota.setDuenio(duenioService.obtenerEntidad(dto.getDuenioId()));
        }

        return mascotaMapper.toDTO(mascotaRepository.save(mascota));
    }

    @Transactional
    public void eliminarMascota(Long id) {
        mascotaRepository.delete(obtenerEntidad(id));
    }
}
