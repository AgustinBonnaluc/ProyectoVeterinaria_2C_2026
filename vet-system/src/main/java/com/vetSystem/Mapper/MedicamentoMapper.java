package com.vetSystem.Mapper;

import com.vetSystem.DTO.MedicamentoRequestDTO;
import com.vetSystem.DTO.MedicamentoResponseDTO;
import com.vetSystem.Entity.Medicamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicamentoMapper {

    MedicamentoResponseDTO toDTO(Medicamento medicamento);

    @Mapping(target = "id", ignore = true)
    Medicamento toEntity(MedicamentoRequestDTO dto);
}
