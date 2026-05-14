package com.example.Qatu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.HeatmapResponseDTO;
import com.example.Qatu.service.impl.HeatmapService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mapa")
@RequiredArgsConstructor
public class MapaController {

    private final HeatmapService heatmapService;

    // GET /api/mapa/heatmap — cualquier autenticado
    @GetMapping("/heatmap")
    public ResponseEntity<HeatmapResponseDTO> obtenerHeatmap() {
        return ResponseEntity.ok(heatmapService.calcularHeatmap());
    }
}