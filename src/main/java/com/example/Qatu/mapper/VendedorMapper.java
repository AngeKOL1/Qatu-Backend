package com.example.Qatu.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.Qatu.dto.VendedorMapaDTO;
import com.example.Qatu.dto.VendedorPerfilDTO;
import com.example.Qatu.dto.VendedorRegisterDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.models.Vendedor;

@Mapper(componentModel = "spring")
public interface VendedorMapper {

    // Mapeamos de VendedorRegisterDTO a Vendedor, ignorando campos que no vienen en
    // el DTO o que se generan automáticamente
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "productos", ignore = true)
    @Mapping(target = "ubicaciones", ignore = true)
    @Mapping(target = "notificaciones", ignore = true)
    @Mapping(target = "reportes", ignore = true)
    @Mapping(target = "fotoPerfilUrl", ignore = true)
    @Mapping(target = "visible", ignore = true)
    @Mapping(target = "fcmToken", ignore = true)
    Vendedor toVendedor(VendedorRegisterDTO dto);

    // Mapeamos de Vendedor a VendedorResponseDTO, extrayendo el nombre de la
    // categoría
    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    @Mapping(target = "tipoMovilidad", source = "tipoMovilidad")
    @Mapping(target = "estado", source = "estado")
    VendedorResponseDTO toResponseDTO(Vendedor vendedor);

    @Mapping(target = "nombreNegocio", source = "nombre")
    @Mapping(target = "categoria", source = "categoria.nombre")
    @Mapping(target = "movilidad", source = "tipoMovilidad")
    @Mapping(target = "lat", ignore = true) // se asigna en el service
    @Mapping(target = "lng", ignore = true) // se asigna en el service
    VendedorMapaDTO toMapaDTO(Vendedor vendedor);

    @Mapping(target = "nombreNegocio", source = "nombre")
    @Mapping(target = "categoria", source = "categoria.nombre")
    @Mapping(target = "movilidad", source = "tipoMovilidad")
    @Mapping(target = "horarioInicio", source = "horarioInicio")
    @Mapping(target = "horarioFin", source = "horarioFin")
    @Mapping(target = "lat", ignore = true) // se asigna en el service
    @Mapping(target = "lng", ignore = true) // se asigna en el service
    @Mapping(target = "productos", ignore = true) // se asigna en el service
    VendedorPerfilDTO toPerfilDTO(Vendedor vendedor);
}
