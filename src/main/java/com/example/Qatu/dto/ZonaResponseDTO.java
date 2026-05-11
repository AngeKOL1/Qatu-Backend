package com.example.Qatu.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ZonaResponseDTO.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonaResponseDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String tipoZona;
    private Integer capacidadMaxima;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
    private Integer administradorId;
    // GeoJSON del polígono para el frontend
    private List<List<Double>> coordenadas;
}
