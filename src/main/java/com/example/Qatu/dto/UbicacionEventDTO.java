// UbicacionEventDTO.java — evento que se emite cuando un vendedor mueve su pin
package com.example.Qatu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionEventDTO {

    private String evento = "UBICACION_ACTUALIZADA";
    private Integer vendedorId;
    private String nombreNegocio;
    private String categoria;
    private Double lat;
    private Double lng;
    private Boolean visible;
    private LocalDateTime timestamp;
}
