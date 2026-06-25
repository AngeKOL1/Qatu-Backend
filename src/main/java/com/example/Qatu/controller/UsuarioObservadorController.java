package com.example.Qatu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.UsuarioObservadorPerfilDTO;
import com.example.Qatu.security.JwtTokenUtil;
import com.example.Qatu.service.IUsuarioObservadorService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/observadores")
@AllArgsConstructor
public class UsuarioObservadorController {
    private final IUsuarioObservadorService service;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("mi-perfil")
    public ResponseEntity<UsuarioObservadorPerfilDTO> obtenerPerfil(
            @RequestHeader("Authorization") String token) {

        Integer usuarioObservadorId = jwtTokenUtil.getIdUsuarioFromToken(token);
        UsuarioObservadorPerfilDTO response = service.obtenerPerfilUsuarioObservador(usuarioObservadorId);

        return ResponseEntity.ok(response);
    }
}
