package com.example.Qatu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.ZonaRequestDTO;
import com.example.Qatu.dto.ZonaResponseDTO;
import com.example.Qatu.models.enums.TipoZona;
import com.example.Qatu.security.JwtTokenUtil;
import com.example.Qatu.service.IZonaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/zonas")
@RequiredArgsConstructor
public class ZonaController {

    private final IZonaService zonaService;
    private final JwtTokenUtil jwtTokenUtil;

    // ── Públicos — cualquier autenticado ──────────────────────────────────────

    // GET /api/zonas/mapa
    @GetMapping("/mapa")
    public ResponseEntity<List<ZonaResponseDTO>> listarZonas(
            @RequestParam(required = false) TipoZona tipo) {

        List<ZonaResponseDTO> zonas = tipo != null
            ? zonaService.listarPorTipo(tipo)
            : zonaService.listarZonasActivas();

        return ResponseEntity.ok(zonas);
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    // POST /api/admin/zonas 
    @PostMapping("/admin/zonas")
    public ResponseEntity<ZonaResponseDTO> crear(
            @Valid @RequestBody ZonaRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        Integer adminId = jwtTokenUtil.getIdUsuarioFromToken(token);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(zonaService.crearZona(dto, adminId));
    }

    // PUT /api/zonas/admin/{id}
    @PutMapping("/admin/{id}")
    public ResponseEntity<ZonaResponseDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ZonaRequestDTO dto) {

        return ResponseEntity.ok(zonaService.actualizarZona(id, dto));
    }

    // DELETE /api/zonas/admin/{id}
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Integer id) {
        zonaService.desactivarZona(id);
        return ResponseEntity.noContent().build();
    }
}
