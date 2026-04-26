package com.example.Qatu.models;

import java.time.LocalDate;


import com.example.Qatu.models.enums.EstadoObservador;

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
@Table(name = "usuarios_observadores")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UsuarioObservador {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(length = 8, nullable = false)
    private String dni;
    @Column(length = 100, unique = true, nullable = false)
    private String email;
    @Column(length = 20, nullable = false, unique = true)
    private String telefono;
    @Column(length = 255, nullable = false)
    private EstadoObservador estado;
    @Column(nullable = false)
    private LocalDate fechaRegistro;
    @Column(nullable = true)
    private LocalDate fechaUltimaActividad;
}
