package com.example.Qatu.models;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Polygon;

import com.example.Qatu.models.enums.TipoZona;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "zonas")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Zona {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoZona tipoZona;

    // Polígono GeoJSON — define el área de la zona
    @Column(columnDefinition = "geography(Polygon, 4326)", nullable = false)
    private Polygon geometria;

    @Column(nullable = false)
    private Integer capacidadMaxima;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = true) 
    private LocalDateTime fechaExpiracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_id", nullable = false)
    private Administrador administrador;

    @PrePersist
    public void prePersist() {
        this.activa = true;
        this.fechaCreacion = LocalDateTime.now();
    }
}