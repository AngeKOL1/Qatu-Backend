package com.example.Qatu.service;

import com.example.Qatu.dto.UsuarioObservadorRegisterDTO;
import com.example.Qatu.dto.UsuarioObservadorResponseDTO;
import com.example.Qatu.mapper.UsuarioObservadorMapper;
import com.example.Qatu.models.UsuarioObservador;
import com.example.Qatu.repository.UsuarioObservadorRepo;
import com.example.Qatu.service.impl.UsuarioObservadorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioObservadorServiceTest {

    @Mock
    private UsuarioObservadorRepo repo;

    @Mock
    private UsuarioObservadorMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioObservadorService usuarioObservadorService;

    private UsuarioObservadorRegisterDTO dto;
    private UsuarioObservador usuarioObservador;
    private UsuarioObservadorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // DTO de prueba
        dto = new UsuarioObservadorRegisterDTO();
        dto.setNombre("Ana");
        dto.setDni("12345678");
        dto.setEmail("ana@email.com");
        dto.setTelefono("987654321");
        dto.setPassword("Password@123");
        dto.setConfirmPassword("Password@123");

        // Entidad simulada
        usuarioObservador = new UsuarioObservador();
        usuarioObservador.setId(1);
        usuarioObservador.setNombre("Ana");
        usuarioObservador.setEmail("ana@email.com");
        usuarioObservador.setPassword("$2a$10$hashedpassword");

        // Response simulado
        responseDTO = new UsuarioObservadorResponseDTO();
        responseDTO.setId(1);
        responseDTO.setNombre("Ana");
        responseDTO.setEmail("ana@email.com");
    }

    // ── CASO 1: Registro exitoso ──────────────────────────────────────────────
    @Test
    @DisplayName("Registro exitoso de usuarioObservador con datos válidos")
    void registrar_exitoso() {

        // Arrange
        when(repo.existsByEmail(dto.getEmail())).thenReturn(false);
        when(mapper.toRegister(dto)).thenReturn(usuarioObservador);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("$2a$10$hashedpassword");
        when(repo.save(any(UsuarioObservador.class))).thenReturn(usuarioObservador);
        when(mapper.toResponse(usuarioObservador)).thenReturn(responseDTO);

        // Act
        UsuarioObservadorResponseDTO resultado = 
            usuarioObservadorService.registrarUsuarioObservador(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNombre());
        assertEquals("ana@email.com", resultado.getEmail());

        verify(repo, times(1)).existsByEmail(dto.getEmail());
        verify(passwordEncoder, times(1)).encode(dto.getPassword());
        verify(repo, times(1)).save(any(UsuarioObservador.class));
        verify(mapper, times(1)).toResponse(usuarioObservador);
    }

    // ── CASO 2: Email duplicado ───────────────────────────────────────────────
    @Test
    @DisplayName("Lanza excepción si el email ya está registrado")
    void registrar_emailDuplicado() {

        // Arrange
        when(repo.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioObservadorService.registrarUsuarioObservador(dto)
        );

        assertEquals("El email ya está registrado", ex.getMessage());
        verify(repo, never()).save(any());
        verify(mapper, never()).toRegister(any());
    }

    // ── CASO 3: Contraseñas no coinciden ─────────────────────────────────────
    @Test
    @DisplayName("Lanza excepción si las contraseñas no coinciden")
    void registrar_passwordsNoCoinciden() {

        // Arrange
        dto.setConfirmPassword("OtraPassword@456");
        when(repo.existsByEmail(dto.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioObservadorService.registrarUsuarioObservador(dto)
        );

        assertEquals("Las contraseñas no coinciden", ex.getMessage());
        verify(repo, never()).save(any());
    }

    // ── CASO 4: La contraseña se encripta ────────────────────────────────────
    @Test
    @DisplayName("La contraseña se guarda encriptada con BCrypt")
    void registrar_passwordEncriptada() {

        // Arrange
        when(repo.existsByEmail(dto.getEmail())).thenReturn(false);
        when(mapper.toRegister(dto)).thenReturn(usuarioObservador);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("$2a$10$hashedpassword");
        when(repo.save(any())).thenReturn(usuarioObservador);
        when(mapper.toResponse(any())).thenReturn(responseDTO);

        // Act
        usuarioObservadorService.registrarUsuarioObservador(dto);

        // Assert
        verify(passwordEncoder, times(1)).encode("Password@123");
        assertNotEquals("Password@123", usuarioObservador.getPassword());
    }

    // ── CASO 5: Solo se hace un save ─────────────────────────────────────────
    @Test
    @DisplayName("El repositorio solo guarda una vez")
    void registrar_soloUnSave() {

        // Arrange
        when(repo.existsByEmail(dto.getEmail())).thenReturn(false);
        when(mapper.toRegister(dto)).thenReturn(usuarioObservador);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
        when(repo.save(any())).thenReturn(usuarioObservador);
        when(mapper.toResponse(any())).thenReturn(responseDTO);

        // Act
        usuarioObservadorService.registrarUsuarioObservador(dto);

        // Assert — verifica que save se llama exactamente 1 vez
        verify(repo, times(1)).save(any(UsuarioObservador.class));
    }

    // ── CASO 6: Contraseña vacía en confirmación ──────────────────────────────
    @Test
    @DisplayName("Lanza excepción si confirmPassword está vacía")
    void registrar_confirmPasswordVacia() {

        // Arrange
        dto.setConfirmPassword("");
        when(repo.existsByEmail(dto.getEmail())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioObservadorService.registrarUsuarioObservador(dto)
        );

        assertEquals("Las contraseñas no coinciden", ex.getMessage());
        verify(repo, never()).save(any());
    }
}