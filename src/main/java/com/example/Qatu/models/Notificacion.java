package com.example.Qatu.models;

import java.time.LocalDate;

import com.example.Qatu.models.enums.TipoMensaje;

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
@Table(name = "notificaciones")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notificacion {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private TipoMensaje tipoMensaje;
    @Column(nullable = false, length = 200)
    private String mensaje;
    @Column(nullable = false, length = 100)
    private String titulo;
    @Column(nullable = false)
    private Boolean leido;
    @Column(nullable = false)
    private LocalDate creDate;
}
