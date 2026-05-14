package com.example.Qatu.models;

import java.time.LocalDateTime;

import com.example.Qatu.models.enums.TipoMensaje;

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
@Table(name = "notificaciones")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notificacion {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMensaje tipoMensaje;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 200)
    private String mensaje;

    @Column(nullable = false)
    private Boolean leido = false;        // ← valor por defecto

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;      // ← LocalDateTime no LocalDate

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Vendedor vendedor;

    @PrePersist
    public void prePersist() {
        this.leido     = false;
        this.createdAt = LocalDateTime.now();
    }
}
