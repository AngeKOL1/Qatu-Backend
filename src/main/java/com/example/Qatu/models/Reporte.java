package com.example.Qatu.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "reportes")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reporte {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String asunto;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, length = 500)
    private String respuesta;           // ← respuesta del admin

    @Column(nullable = false, length = 20)
    private String estado;              // ABIERTO | EN_REVISION | CERRADO

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;    // ← LocalDateTime no LocalDate

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "idVendedor", nullable = false)
    private Vendedor vendedor;

    @ManyToOne
    @JoinColumn(name = "idAdministrador", nullable = true) // ← nullable true
    private Administrador administrador;

    @PrePersist
    public void prePersist() {
        this.estado    = "ABIERTO";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}