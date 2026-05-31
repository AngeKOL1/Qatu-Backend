package com.example.Qatu.dto;

import java.util.List;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// VendedorPerfilDTO.java — perfil completo con catálogo
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedorPerfilDTO {
    private Integer id;
    private String nombre;
    private String nombreNegocio;
    private String descripcion;
    private String fotoPerfilUrl;
    private String categoria;
    private String movilidad;
    private String horarioInicio;
    private String horarioFin;
    private Boolean visible;
    private Double lat;
    private Double lng;
    private List<ProductoResponseDTO> productos;
}
