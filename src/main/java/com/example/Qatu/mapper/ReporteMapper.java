package com.example.Qatu.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.models.Reporte;

@Mapper(componentModel = "spring")
public interface ReporteMapper {
    
    @Mapping(target = "vendedorId",     source = "vendedor.id")
    @Mapping(target = "vendedorNombre", source = "vendedor.nombre")
    @Mapping(target = "estado", ignore = true)
    ReporteResponseDTO toResponseDTO(Reporte reporte);
}
