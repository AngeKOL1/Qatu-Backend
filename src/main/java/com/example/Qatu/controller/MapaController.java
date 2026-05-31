package com.example.Qatu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.HeatmapResponseDTO;
import com.example.Qatu.dto.VendedorMapaDTO;
import com.example.Qatu.service.IVendedorService;
import com.example.Qatu.service.impl.HeatmapService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mapa")
@RequiredArgsConstructor
public class MapaController {

    private final HeatmapService heatmapService;
    private final IVendedorService vendedorService;

    // GET /api/mapa/heatmap — cualquier autenticado
    @GetMapping("/heatmap")
    public ResponseEntity<HeatmapResponseDTO> obtenerHeatmap() {
        return ResponseEntity.ok(heatmapService.calcularHeatmap());
    }

    // GET /api/mapa/vendedores?categoria=COMIDA
    @GetMapping("/vendedores")
    public ResponseEntity<List<VendedorMapaDTO>> listarVendedores(
            @RequestParam(required = false) String categoria) {

        return ResponseEntity.ok(
            vendedorService.listarVendedoresActivosEnMapa(categoria));
    }
}