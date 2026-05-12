package com.example.Qatu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// HeatmapPuntoDTO.java — cada punto de densidad
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapPuntoDTO {
    private Double lat;
    private Double lng;
    private Integer vendedoresCount;
    private String nivel; // ROJO | AMARILLO | VERDE
}