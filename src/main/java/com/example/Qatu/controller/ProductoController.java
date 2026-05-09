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
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.ProductoRegisterDTO;
import com.example.Qatu.dto.ProductoResponseDTO;
import com.example.Qatu.dto.ProductoUpdateDTO;
import com.example.Qatu.security.JwtTokenUtil;
import com.example.Qatu.service.impl.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final JwtTokenUtil jwtTokenUtil;

    // GET /api/vendedores/{id}/productos — público
    @GetMapping("/{id}/productos")
    public ResponseEntity<List<ProductoResponseDTO>> listar(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                productoService.listarProductosActivos(id));
    }

    // POST /api/vendedores/mis-productos — solo VENDEDOR
    @PostMapping("/mis-productos")
    public ResponseEntity<ProductoResponseDTO> crear(
            @Valid @RequestBody ProductoRegisterDTO dto,
            @RequestHeader("Authorization") String token) {

        Integer vendedorId = jwtTokenUtil.getIdUsuarioFromToken(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearProducto(vendedorId, dto));
    }

    // PUT /api/vendedores/mis-productos/{pid} — solo VENDEDOR
    @PutMapping("/mis-productos/{pid}")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @PathVariable Integer pid,
            @Valid @RequestBody ProductoUpdateDTO dto,
            @RequestHeader("Authorization") String token) {

        Integer vendedorId = jwtTokenUtil.getIdUsuarioFromToken(token);
        return ResponseEntity.ok(
                productoService.actualizarProducto(vendedorId, pid, dto));
    }

    // DELETE /api/vendedores/mis-productos/{pid} — solo VENDEDOR
    @DeleteMapping("/mis-productos/{pid}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer pid,
            @RequestHeader("Authorization") String token) {

        Integer vendedorId = jwtTokenUtil.getIdUsuarioFromToken(token);
        productoService.eliminarProducto(vendedorId, pid);
        return ResponseEntity.noContent().build();
    }
}
