package com.example.Qatu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Qatu.dto.ActualizarReporteDTO;
import com.example.Qatu.dto.CambiarEstadoVendedorDTO;
import com.example.Qatu.dto.PaginaResponseDTO;
import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.dto.UmbralRequestDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.service.IAdministradorService;
import com.example.Qatu.service.impl.HeatmapService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdministradorController {

    private final IAdministradorService administradorService;
    private final HeatmapService heatmapService;

    // ── Vendedores ────────────────────────────────────────────────────────────

    // GET /api/admin/vendedores?estado=PENDIENTE
    @GetMapping("/vendedores")
    public ResponseEntity<PaginaResponseDTO<VendedorResponseDTO>> listarVendedores(
            @RequestParam(required = false) EstadoVendedor estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) {

        return ResponseEntity.ok(
                administradorService.listarVendedores(estado, pagina, tamanio));
    }

    // PATCH /api/admin/vendedores/{id}/estado
    @PatchMapping("/vendedores/{id}/estado")
    public ResponseEntity<VendedorResponseDTO> cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody CambiarEstadoVendedorDTO dto) {

        return ResponseEntity.ok(
                administradorService.cambiarEstadoVendedor(id, dto.getNuevoEstado()));
    }

    // ── Reportes ──────────────────────────────────────────────────────────────

    // GET /api/admin/reportes?estado=ABIERTO
    @GetMapping("/reportes")
    public ResponseEntity<PaginaResponseDTO<ReporteResponseDTO>> listarReportes(
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) {

        return ResponseEntity.ok(
                administradorService.listarReportes(estado, pagina, tamanio));
    }

    // PATCH /api/admin/reportes/{id}
    @PatchMapping("/reportes/{id}")
    public ResponseEntity<ReporteResponseDTO> actualizarReporte(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarReporteDTO dto) {

        return ResponseEntity.ok(
                administradorService.actualizarReporte(
                        id, dto.getEstado(), dto.getRespuesta()));
    }

    // PATCH /api/admin/heatmap/umbral
    @PatchMapping("/heatmap/umbral")
    public ResponseEntity<Void> actualizarUmbral(
            @Valid @RequestBody UmbralRequestDTO dto) {

        heatmapService.actualizarUmbral(dto.getUmbralRojo(), dto.getUmbralAmarillo());
        return ResponseEntity.ok().build();
    }

}