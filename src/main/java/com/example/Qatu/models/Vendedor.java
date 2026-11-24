package com.example.Qatu.models;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.models.enums.Movilidad;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
    @Column(length = 8, nullable = false, unique = true)
    private String dni;
    @Column(nullable = false)
    @ToString.Exclude
    private String password;
    @Column(length = 20, nullable = false, unique = true)
    private String telefono;
    @Column(length = 255, nullable = true)
    private String descripcion;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Movilidad tipoMovilidad;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoVendedor estado;
    @Column(nullable = false)
    private LocalTime horarioInicio; 
    @Column(nullable = false)
    private LocalTime horarioFin;
    @Column(nullable = true)
    private String fotoPerfilUrl;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private Boolean visible = false;
    @Column(nullable = true, length = 500)
    @ToString.Exclude
    private String fcmToken;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ToString.Exclude
    @OneToMany(mappedBy = "vendedor")
    private List<Producto> productos;

    @ToString.Exclude
    @OneToMany(mappedBy = "vendedor")
    private List<Ubicacion> ubicaciones;

    @ToString.Exclude
    @OneToMany(mappedBy = "vendedor")
    private List<Notificacion> notificaciones;

    @ToString.Exclude
    @OneToMany(mappedBy = "vendedor")
    private List<Reporte> reportes;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.estado = EstadoVendedor.PENDIENTE;
        this.visible = false;
        this.productos = new ArrayList<>();
        this.ubicaciones = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.reportes = new ArrayList<>();
    }

    // Se ejecuta automáticamente antes de actualizar
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
