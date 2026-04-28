package com.example.Qatu.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// UbicacionResponseDTO.java — lo que devuelve el endpoint
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionResponseDTO {
    private Integer ubicacionId;
    private Integer vendedorId;
    private Double lat;
    private Double lng;
    private LocalDateTime timestamp;
}
