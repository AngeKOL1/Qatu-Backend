package com.example.Qatu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.UbicacionRequestDTO;
import com.example.Qatu.dto.UbicacionResponseDTO;
import com.example.Qatu.security.JwtTokenUtil;
import com.example.Qatu.service.IUbicacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendedores")
@RequiredArgsConstructor
public class UbicacionController {

    private final IUbicacionService ubicacionService;
    private final JwtTokenUtil jwtTokenUtil;

    @PatchMapping("/mi-ubicacion")
    public ResponseEntity<UbicacionResponseDTO> actualizarUbicacion(
            @Valid @RequestBody UbicacionRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        // Extraemos el id directamente del token 
        Integer vendedorId = jwtTokenUtil.getIdUsuarioFromToken(token);

        UbicacionResponseDTO response = ubicacionService.actualizarUbicacion(vendedorId, dto);
        return ResponseEntity.ok(response);
    }
}
