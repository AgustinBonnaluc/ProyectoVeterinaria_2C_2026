package com.vetSystem.Service;

import com.vetSystem.DTO.DuenioDTO;
import com.vetSystem.Entity.Duenio;
import com.vetSystem.Exception.DuplicateResourceException;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Mapper.DuenioMapper;
import com.vetSystem.Repository.DuenioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DuenioService {

    private final DuenioRepository duenioRepository;
    private final DuenioMapper duenioMapper;

    /**
     * Uso INTERNO entre services (MascotaService lo necesita para setear la relación).
     * No sale nunca hacia el Controller.
     */
    @Transactional(readOnly = true)
    public Duenio obtenerEntidad(Long id) {
        return duenioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño", id));
    }

    @Transactional(readOnly = true)
    public List<DuenioDTO> listarTodos() {
        return duenioRepository.findAll()
                .stream()
                .map(duenioMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DuenioDTO buscarPorId(Long id) {
        return duenioMapper.toDTO(obtenerEntidad(id));
    }

    @Transactional
    public DuenioDTO registrarDuenio(DuenioDTO dto) {
        if (duenioRepository.existsByCedula(dto.getCedula())) {
            throw new DuplicateResourceException("Dueño", "cédula", dto.getCedula());
        }
        Duenio duenio = duenioMapper.toEntity(dto);
        duenio.setId(null); // el cliente no decide el ID, aunque lo mande en el JSON
        return duenioMapper.toDTO(duenioRepository.save(duenio));
    }

    @Transactional
    public DuenioDTO modificarDuenio(Long id, DuenioDTO dto) {
        Duenio duenio = obtenerEntidad(id);
        duenio.setNombre(dto.getNombre());
        duenio.setApellido(dto.getApellido());
        duenio.setTelefono(dto.getTelefono());
        duenio.setEmail(dto.getEmail());
        // la cédula NO se modifica: es la clave natural del dueño
        return duenioMapper.toDTO(duenioRepository.save(duenio));
    }

    @Transactional
    public void eliminarDuenio(Long id) {
        duenioRepository.delete(obtenerEntidad(id));
    }
}



