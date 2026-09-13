package com.vetSystem.Mapper;

import com.vetSystem.DTO.MascotaDTO;
import com.vetSystem.Entity.Mascota;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MascotaMapper {

    @Mapping(source = "duenio.id",     target = "duenioId")
    @Mapping(source = "duenio.nombre", target = "duenioNombre")
    MascotaDTO toDTO(Mascota mascota);

    // El dueño NO se mapea desde el DTO: el Service lo busca en la base por duenioId
    @Mapping(target = "duenio", ignore = true)
    Mascota toEntity(MascotaDTO dto);
}