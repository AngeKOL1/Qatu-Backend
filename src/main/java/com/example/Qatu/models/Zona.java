package com.example.Qatu.models;

import java.time.LocalDate;

import org.geolatte.geom.Point;

import com.example.Qatu.models.enums.TipoZona;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
    @Column(nullable = false, length = 255)
    private String descripcion;
    @Column(nullable = false)
    private TipoZona tipoZona;
    @Column(columnDefinition = "geography(Point, 4326)", nullable = false)
    private Point coordenada;
    @Column(nullable = false)
    private Integer capacidadMaxima;
    @Column(nullable = false)
    private Boolean activo;
    @Column(nullable = false)
    private LocalDate fechaCreacion;
    @Column(nullable = false)
    private LocalDate fechaExpiracion;
}
