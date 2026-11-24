package com.example.Qatu.models;

import java.time.LocalDateTime;
import java.util.List;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ubicaciones")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ubicacion {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // El Point ya contiene lat y lng 
    @Column(columnDefinition = "geography(Point, 4326)", nullable = false)
    private Point coordenada;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Boolean activo = true;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @ToString.Exclude
    @OneToMany(mappedBy = "ubicacion")
    private List<SugerenciaReasignacion> sugerenciasReasignacion;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
        this.activo = true;
    }
}