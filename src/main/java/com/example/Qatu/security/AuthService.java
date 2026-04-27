package com.example.Qatu.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.example.Qatu.dto.LoginRequestDTO;
import com.example.Qatu.dto.LoginResponseDTO;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;

    public LoginResponseDTO autenticar(LoginRequestDTO dto) {

        // 1. Autenticar credenciales
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
        } catch (DisabledException e) {
            throw new RuntimeException("Usuario deshabilitado");
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 2. Cargar el usuario desde la tabla correcta
        CustomUserDetails userDetails = (CustomUserDetails) 
            jwtUserDetailsService.loadUserByUsername(dto.getEmail());

        // 3. Generar token con id incluido
        String token = jwtTokenUtil.generateToken(userDetails, userDetails.getId());

        // 4. Armar respuesta
        return LoginResponseDTO.builder()
            .token(token)
            .id(userDetails.getId())
            .nombre(userDetails.getNombre())
            .email(userDetails.getUsername())
            .rol(userDetails.getRol())
            .build();
    }
}