package com.example.Qatu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// UmbralRequestDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UmbralRequestDTO {

    @NotNull(message = "El umbral rojo es obligatorio")
    @Min(value = 1, message = "El umbral rojo debe ser mayor a 0")
    private Integer umbralRojo;

    @NotNull(message = "El umbral amarillo es obligatorio")
    @Min(value = 1, message = "El umbral amarillo debe ser mayor a 0")
    private Integer umbralAmarillo;
}