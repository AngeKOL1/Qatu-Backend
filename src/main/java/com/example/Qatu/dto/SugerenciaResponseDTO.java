package com.example.Qatu.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// SugerenciaResponseDTO.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SugerenciaResponseDTO {
    private Integer id;
    private Integer vendedorId;
    private String  nombreVendedor;
    private Integer zonaId;
    private String  nombreZona;
    private String  tipoZona;
    private String  estado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaRespuesta;
}