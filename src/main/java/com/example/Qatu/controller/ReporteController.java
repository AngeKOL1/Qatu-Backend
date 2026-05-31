package com.example.Qatu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.ReporteRequestDTO;
import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.security.JwtTokenUtil;
import com.example.Qatu.service.IReporteService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@AllArgsConstructor
public class ReporteController {

    private final JwtTokenUtil jwtTokenUtil;
    private final IReporteService reporteService;

    // POST /api/reportes — vendedor crea reporte
    @PostMapping("/mis-reportes")
    public ResponseEntity<ReporteResponseDTO> crearReporte(
            @Valid @RequestBody ReporteRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        Integer vendedorId = jwtTokenUtil.getIdUsuarioFromToken(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reporteService.crearReporte(vendedorId, dto));
    }

    // GET /api/reportes/mis-reportes — vendedor ve sus reportes
    @GetMapping("/mis-reportes")
    public ResponseEntity<List<ReporteResponseDTO>> listarMisReportes(
            @RequestHeader("Authorization") String token) {

        Integer vendedorId = jwtTokenUtil.getIdUsuarioFromToken(token);
        return ResponseEntity.ok(reporteService.listarMisReportes(vendedorId));
    }
}
