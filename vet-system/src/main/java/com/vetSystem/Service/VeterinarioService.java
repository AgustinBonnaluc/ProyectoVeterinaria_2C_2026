package com.vetSystem.Service;

import com.vetSystem.DTO.VeterinarioDTO;
import com.vetSystem.Entity.Veterinario;
import com.vetSystem.Exception.DuplicateResourceException;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Mapper.VeterinarioMapper;
import com.vetSystem.Repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final VeterinarioMapper veterinarioMapper;

    @Transactional(readOnly = true)
    public Veterinario obtenerEntidad(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario", id));
    }

    @Transactional(readOnly = true)
    public List<VeterinarioDTO> listarTodos() {
        return veterinarioRepository.findAll()
                .stream()
                .map(veterinarioMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VeterinarioDTO buscarPorId(Long id) {
        return veterinarioMapper.toDTO(obtenerEntidad(id));
    }

    @Transactional
    public VeterinarioDTO registrarVeterinario(VeterinarioDTO dto) {
        if (veterinarioRepository.existsByMatricula(dto.getMatricula())) {
            throw new DuplicateResourceException(
                    "Veterinario", "matrícula", dto.getMatricula());
        }
        Veterinario veterinario = veterinarioMapper.toEntity(dto);
        veterinario.setId(null);
        return veterinarioMapper.toDTO(veterinarioRepository.save(veterinario));
    }

    @Transactional
    public VeterinarioDTO modificarVeterinario(Long id, VeterinarioDTO dto) {
        Veterinario veterinario = obtenerEntidad(id);
        veterinario.setNombre(dto.getNombre());
        veterinario.setApellido(dto.getApellido());
        veterinario.setEspecialidad(dto.getEspecialidad());
        // la matrícula NO se modifica: es la identidad profesional, otorgada por el colegio
        return veterinarioMapper.toDTO(veterinarioRepository.save(veterinario));
    }

    @Transactional
    public void eliminarVeterinario(Long id) {
        veterinarioRepository.delete(obtenerEntidad(id));
    }
}