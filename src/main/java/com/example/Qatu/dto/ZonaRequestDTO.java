package com.example.Qatu.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.Qatu.models.enums.TipoZona;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ZonaRequestDTO.java — lo que recibe el endpoint del admin
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El tipo de zona es obligatorio")
    private TipoZona tipoZona;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 0, message = "La capacidad no puede ser negativa") // ← 0 en lugar de 1
    private Integer capacidadMaxima;

    private LocalDateTime fechaExpiracion; // null = permanente

    // Coordenadas del polígono — lista de [lng, lat]
    // Ejemplo:
    // [[-78.501,-7.163],[-78.500,-7.163],[-78.500,-7.164],[-78.501,-7.163]]
    @NotNull(message = "Las coordenadas del polígono son obligatorias")
    private List<List<Double>> coordenadas;
}