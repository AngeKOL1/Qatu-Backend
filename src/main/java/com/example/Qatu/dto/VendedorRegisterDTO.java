package com.example.Qatu.dto;

import java.time.LocalTime;

import com.example.Qatu.models.enums.Movilidad;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendedorRegisterDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 caracteres")
    private String dni;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    private String descripcion;

    @NotNull(message = "El tipo de movilidad es obligatorio")
    private Movilidad tipoMovilidad;

    @NotNull(message = "El horario de inicio es obligatorio")
    private LocalTime horarioInicio;

    @NotNull(message = "El horario de fin es obligatorio")
    private LocalTime horarioFin;

    @NotNull(message = "La categoría es obligatoria")
    private String nombreCategoria;
}

