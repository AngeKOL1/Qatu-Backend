package com.example.Qatu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoActividadDTO {

    @NotNull(message = "El campo visible es obligatorio")
    private Boolean visible;
}