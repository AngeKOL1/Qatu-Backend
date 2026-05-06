package com.example.Qatu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarReporteDTO {
    @NotBlank(message = "El estado del reporte no puede estar vacío")
    private String estado;
    private String respuesta;
}
