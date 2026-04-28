package com.example.Qatu.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Qatu.dto.UsuarioObservadorRegisterDTO;
import com.example.Qatu.dto.UsuarioObservadorResponseDTO;
import com.example.Qatu.mapper.UsuarioObservadorMapper;
import com.example.Qatu.models.UsuarioObservador;
import com.example.Qatu.repository.UsuarioObservadorRepo;
import com.example.Qatu.service.IUsuarioObservadorService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioObservadorService extends GenericService<UsuarioObservador, Integer> implements IUsuarioObservadorService {
    private final UsuarioObservadorRepo repo;
    private final UsuarioObservadorMapper mapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    protected UsuarioObservadorRepo getRepo() {
        return repo;
    }

    @Override
    public UsuarioObservadorResponseDTO registrarUsuarioObservador(UsuarioObservadorRegisterDTO dto){
        // Validamos si el email ya existe
        if(repo.existsByEmail(dto.getEmail())){
            throw new IllegalArgumentException("El email ya está registrado");
        }
        // Validamos la confirmación de contraseña
        if(!dto.getPassword().equals(dto.getConfirmPassword()) ){
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        // Mapeamos el dto a la entidad
        UsuarioObservador usuarioObservador = mapper.toRegister(dto);
        // Encriptamos la contraseña
        usuarioObservador.setPassword(passwordEncoder.encode(dto.getPassword()));
        //Guardamos el nuevo usuario observador
        UsuarioObservadorResponseDTO responseDTO = mapper.toResponse(repo.save(usuarioObservador));
        return responseDTO;
    }
}
