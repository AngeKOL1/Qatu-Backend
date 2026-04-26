package com.example.Qatu.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "administradores")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false, length = 255)
    private String password;
    @Column(nullable = false)
    private LocalDate createdAt;

    @OneToMany(mappedBy = "administrador")
    private List<Reporte> reportes;

    @OneToMany(mappedBy = "administrador")
    private List<Zona> zonas;
}
