package com.example.Qatu.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CongestionEventDTO {
    private String evento = "ZONA_CONGESTIONADA";
    private Double lat;
    private Double lng;
    private Integer vendedoresCount;
    private String nivel;   // ROJO | AMARILLO | VERDE
    private LocalDateTime timestamp; 
}
