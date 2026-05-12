package com.example.Qatu.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// HeatmapResponseDTO.java — respuesta completa
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapResponseDTO {
    private List<HeatmapPuntoDTO> puntos;
    private Integer umbralRojo;
    private Integer umbralAmarillo;
    private LocalDateTime calculadoEn;
} 