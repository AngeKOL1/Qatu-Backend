package com.example.Qatu.models;

import java.time.LocalDateTime;
import java.util.List;

import org.locationtech.jts.geom.Point; 

import jakarta.persistence.*;
import lombok.*;

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
    private Boolean activa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idVendedor", nullable = false)
    private Vendedor vendedor;

    @OneToMany(mappedBy = "ubicacion", cascade = CascadeType.ALL)
    private List<SugerenciaReasignacion> sugerenciasReasignacion;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
        this.activa = true;
    }
}