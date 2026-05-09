package com.example.Qatu.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Integer id;
    private String nombre;
    private Double precio;
    private String descripcion;
    private String fotoUrl;
    private Boolean activo;
    private Integer vendedorId;
    private String nombreVendedor;
    private LocalDateTime fechaCreacion;
}