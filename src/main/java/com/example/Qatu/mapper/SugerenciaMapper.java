package com.example.Qatu.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.Qatu.dto.SugerenciaResponseDTO;
import com.example.Qatu.models.SugerenciaReasignacion;

@Mapper(componentModel = "spring")
public interface SugerenciaMapper {

    @Mapping(target = "vendedorId",    source = "vendedor.id")
    @Mapping(target = "nombreVendedor",source = "vendedor.nombre")
    @Mapping(target = "zonaId",        source = "zona.id")
    @Mapping(target = "nombreZona",    source = "zona.nombre")
    @Mapping(target = "tipoZona",      source = "zona.tipoZona")
    @Mapping(target = "estado",        source = "estado")
    SugerenciaResponseDTO toResponseDTO(SugerenciaReasignacion sugerencia);
}