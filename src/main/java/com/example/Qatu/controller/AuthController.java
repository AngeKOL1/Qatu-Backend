package com.example.Qatu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Qatu.dto.LoginRequestDTO;
import com.example.Qatu.dto.LoginResponseDTO;
import com.example.Qatu.dto.UsuarioObservadorRegisterDTO;
import com.example.Qatu.dto.UsuarioObservadorResponseDTO;
import com.example.Qatu.dto.VendedorRegisterDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.mapper.VendedorMapper;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.security.AuthService;
import com.example.Qatu.service.IUsuarioObservadorService;
import com.example.Qatu.service.IVendedorService;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final IVendedorService vendedorService;
    private final VendedorMapper vendedorMapper;
    private final IUsuarioObservadorService usuarioObservadorService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        LoginResponseDTO response = authService.autenticar(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/vendedor")
    public ResponseEntity<VendedorResponseDTO> registrarVendedor(
            @Valid @RequestBody VendedorRegisterDTO dto) {

        Vendedor vendedor = vendedorService.registrarVendedor(dto);
        VendedorResponseDTO response = vendedorMapper.toResponseDTO(vendedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register/observador")
    public ResponseEntity<UsuarioObservadorResponseDTO> registrarUsuarioObservador(
            @Valid @RequestBody UsuarioObservadorRegisterDTO dto) {

        UsuarioObservadorResponseDTO response = usuarioObservadorService.registrarUsuarioObservador(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}