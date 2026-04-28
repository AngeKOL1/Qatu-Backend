package com.example.Qatu.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.Qatu.dto.UsuarioObservadorRegisterDTO;
import com.example.Qatu.dto.UsuarioObservadorResponseDTO;
import com.example.Qatu.models.UsuarioObservador;

@Mapper(componentModel = "spring")
public interface UsuarioObservadorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "fechaUltimaActividad", ignore = true)
    UsuarioObservador toRegister(UsuarioObservadorRegisterDTO dto);

    UsuarioObservadorResponseDTO toResponse(UsuarioObservador usuarioObservador);
}
