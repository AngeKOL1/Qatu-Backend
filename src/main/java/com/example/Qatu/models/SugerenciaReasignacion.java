package com.example.Qatu.models;

import java.time.LocalDate;

import com.example.Qatu.models.enums.EstadoSugerencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private EstadoSugerencia estado;
    @Column(nullable = false)
    private LocalDate fechaEnvio;
    @Column(nullable = true)
    private LocalDate fechaRespuesta;

    @ManyToOne
    @JoinColumn(name = "idUbicacion", nullable = false)
    private Ubicacion ubicacion;
    
}
