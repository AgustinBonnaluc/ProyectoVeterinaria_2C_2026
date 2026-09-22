package com.vetSystem.Service;

import com.vetSystem.DTO.MedicamentoRequestDTO;
import com.vetSystem.DTO.MedicamentoResponseDTO;
import com.vetSystem.Entity.Medicamento;
import com.vetSystem.Exception.DuplicateResourceException;
import com.vetSystem.Exception.ResourceNotFoundException;
import com.vetSystem.Exception.StockInsuficienteException;
import com.vetSystem.Mapper.MedicamentoMapper;
import com.vetSystem.Repository.MedicamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final MedicamentoMapper medicamentoMapper;

    /** Uso interno entre services (TurnoService lo necesita para recetar). */
    @Transactional(readOnly = true)
    public Medicamento obtenerEntidad(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento", id));
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> listarTodos() {
        return medicamentoRepository.findAll()
                .stream()
                .map(medicamentoMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicamentoResponseDTO buscarPorId(Long id) {
        return medicamentoMapper.toDTO(obtenerEntidad(id));
    }

    @Transactional
    public MedicamentoResponseDTO registrar(MedicamentoRequestDTO dto) {
        if (medicamentoRepository.existsByNombre(dto.getNombre())) {
            throw new DuplicateResourceException("Medicamento", "nombre", dto.getNombre());
        }
        Medicamento medicamento = medicamentoMapper.toEntity(dto);
        return medicamentoMapper.toDTO(medicamentoRepository.save(medicamento));
    }

    @Transactional
    public MedicamentoResponseDTO modificar(Long id, MedicamentoRequestDTO dto) {
        Medicamento medicamento = obtenerEntidad(id);
        medicamento.setNombre(dto.getNombre());
        medicamento.setPrincipioActivo(dto.getPrincipioActivo());
        medicamento.setStock(dto.getStock());
        medicamento.setPrecioUnitario(dto.getPrecioUnitario());
        return medicamentoMapper.toDTO(medicamentoRepository.save(medicamento));
    }

    @Transactional
    public void eliminar(Long id) {
        medicamentoRepository.delete(obtenerEntidad(id));
    }

    @Transactional
    public void descontarUnidad(Medicamento medicamento) {
        if (medicamento.getStock() <= 0) { //si el stock es menor o igual a 0 lanzo la excep
            throw new StockInsuficienteException(medicamento.getId(), medicamento.getNombre());
        }
        medicamento.setStock(medicamento.getStock() - 1);
        medicamentoRepository.save(medicamento);
    }
}
