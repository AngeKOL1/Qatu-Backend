package com.example.Qatu.models;

import java.time.LocalDateTime;

import com.example.Qatu.models.enums.EstadoSugerencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sugerencias_reasignacion")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SugerenciaReasignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoSugerencia estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;     // ← LocalDateTime

    @Column(nullable = true)
    private LocalDateTime fechaRespuesta; // ← LocalDateTime

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;            // ← faltaba relación con vendedor

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id", nullable = false)
    private Zona zona;                    // ← faltaba relación con zona

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;

    @PrePersist
    public void prePersist() {
        this.estado    = EstadoSugerencia.ENVIADA;
        this.fechaEnvio = LocalDateTime.now();
    }
}
