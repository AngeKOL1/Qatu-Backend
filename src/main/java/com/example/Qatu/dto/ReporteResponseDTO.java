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
public class ReporteResponseDTO {
    private Integer id;
    private Integer vendedorId;
    private String vendedorNombre;
    private String asunto;
    private String descripcion;
    private String estado;
    private LocalDateTime createdAt;
}
