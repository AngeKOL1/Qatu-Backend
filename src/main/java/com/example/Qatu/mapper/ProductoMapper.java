package com.example.Qatu.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.Qatu.dto.ProductoRegisterDTO;
import com.example.Qatu.dto.ProductoResponseDTO;
import com.example.Qatu.models.Producto;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "activo",        ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "vendedor",      ignore = true)
    Producto toEntity(ProductoRegisterDTO dto);

    @Mapping(target = "vendedorId",    source = "vendedor.id")
    @Mapping(target = "nombreVendedor",source = "vendedor.nombre")
    ProductoResponseDTO toResponseDTO(Producto producto);
}
