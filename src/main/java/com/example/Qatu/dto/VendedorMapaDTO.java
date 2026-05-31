package com.example.Qatu.dto;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedorMapaDTO {
    private Integer id;
    private String nombreNegocio;
    private String categoria;
    private String movilidad;
    private Double lat;
    private Double lng;
    private Boolean visible;
}