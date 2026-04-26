package com.example.Qatu.models;

import java.time.LocalDate;

import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.models.enums.Movilidad;

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
@Table(name = "vendedores")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vendedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(length =8, nullable = false)
    private String Dni;
    @Column(length = 20, nullable = false)
    private String telefono;
    @Column(length = 255, nullable = true)
    private String descripcion;
    @Column(nullable = false)
    private Movilidad tipoMovilidad;
    @Column(nullable = false)
    private EstadoVendedor estado;
    @Column(nullable = false)
    private LocalDate horarioInicio; 
    @Column(nullable = false)
    private LocalDate horarioFin;
    @Column(nullable = true)
    private String fotoPerfilUrl;
    @Column(nullable = false)
    private LocalDate createdAt; 
    @Column(nullable = false)
    private LocalDate updateAt;
}
