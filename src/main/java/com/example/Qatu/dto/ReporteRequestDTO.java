package com.example.Qatu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ReporteRequestDTO.java — lo que envía el vendedor
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRequestDTO {

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 150, min = 8, message = "El asunto debe tener entre 8 y 150 caracteres")
    private String asunto;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, min = 20, message = "La descripción debe tener entre 20 y 500 caracteres")
    private String descripcion;
}