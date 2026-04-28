package com.example.Qatu.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioObservadorResponseDTO {
    private Integer id;
    private String nombre;
    private String email;
    private String estado;
    private LocalDate fechaRegistro;
}
