package com.vetSystem.Mapper;

import com.vetSystem.DTO.TurnoResponseDTO;
import com.vetSystem.Entity.Turno;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TurnoMapper {

    @Mapping(source = "mascota.id",          target = "mascotaId")
    @Mapping(source = "mascota.nombre",      target = "mascotaNombre")
    @Mapping(source = "veterinario.id",      target = "veterinarioId")
    @Mapping(source = "veterinario.nombre",  target = "veterinarioNombre")
    TurnoResponseDTO toDTO(Turno turno);

    // No hay toEntity(TurnoRequestDTO): el Service arma el Turno a mano
    // porque necesita buscar Mascota y Veterinario en la base primero.
}
