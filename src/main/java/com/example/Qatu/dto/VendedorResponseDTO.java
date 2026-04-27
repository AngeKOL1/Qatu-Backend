package com.example.Qatu.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendedorResponseDTO {
    private Integer id;
    private String nombre;
    private String email;
    private String telefono;
    private String descripcion;
    private String tipoMovilidad;
    private String estado;
    private LocalTime horarioInicio;
    private LocalTime horarioFin;
    private String fotoPerfilUrl;
    private String nombreCategoria;
    private LocalDateTime createdAt; 
}
