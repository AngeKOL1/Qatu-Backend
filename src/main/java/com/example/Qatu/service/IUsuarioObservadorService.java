package com.example.Qatu.service;

import com.example.Qatu.dto.UsuarioObservadorRegisterDTO;
import com.example.Qatu.dto.UsuarioObservadorResponseDTO;
import com.example.Qatu.models.UsuarioObservador;

public interface IUsuarioObservadorService extends IGenericService<UsuarioObservador, Integer> {
    UsuarioObservadorResponseDTO registrarUsuarioObservador(UsuarioObservadorRegisterDTO dto);
}