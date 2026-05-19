package com.example.Qatu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.ResponderSugerenciaDTO;
import com.example.Qatu.dto.SugerenciaResponseDTO;
import com.example.Qatu.security.JwtTokenUtil;
import com.example.Qatu.service.ISugerenciaReasignacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sugerencias")
@RequiredArgsConstructor
public class SugerenciaController {

    private final ISugerenciaReasignacionService sugerenciaService;
    private final JwtTokenUtil jwtTokenUtil;

    // GET /api/sugerencias/mis-sugerencias — vendedor ve su historial
    @GetMapping("/mis-sugerencias")
    public ResponseEntity<List<SugerenciaResponseDTO>> listar(
            @RequestHeader("Authorization") String token) {

        Integer vendedorId = jwtTokenUtil.getIdUsuarioFromToken(token);
        return ResponseEntity.ok(
                sugerenciaService.listarPorVendedor(vendedorId));
    }

    // PATCH /api/sugerencias/{id}/respuesta — vendedor acepta o ignora
    @PatchMapping("/{id}/respuesta")
    public ResponseEntity<SugerenciaResponseDTO> responder(
            @PathVariable Integer id,
            @Valid @RequestBody ResponderSugerenciaDTO dto,
            @RequestHeader("Authorization") String token) {

        Integer vendedorId = jwtTokenUtil.getIdUsuarioFromToken(token);
        return ResponseEntity.ok(
                sugerenciaService.responderSugerencia(
                        id, vendedorId, dto.getAccion()));
    }
}
