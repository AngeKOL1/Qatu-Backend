package com.example.Qatu.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.Qatu.dto.ZonaRequestDTO;
import com.example.Qatu.dto.ZonaResponseDTO;
import com.example.Qatu.models.Zona;

@Mapper(componentModel = "spring")
public interface ZonaMapper {

    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "activa",        ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "geometria",     ignore = true) // se construye en el service
    @Mapping(target = "administrador", ignore = true)
    Zona toEntity(ZonaRequestDTO dto);

    @Mapping(target = "administradorId", source = "administrador.id")
    @Mapping(target = "tipoZona",        source = "tipoZona")
    @Mapping(target = "coordenadas",     ignore = true) // se construye en el service
    ZonaResponseDTO toResponseDTO(Zona zona);
}