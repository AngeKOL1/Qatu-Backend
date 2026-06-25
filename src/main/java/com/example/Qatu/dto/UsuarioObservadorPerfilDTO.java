package com.example.Qatu.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioObservadorPerfilDTO {
    private Integer id;
    private String nombre;
    private String dni;
    private String email;
    private String telefono;
    private LocalDate fechaRegistro;
}
