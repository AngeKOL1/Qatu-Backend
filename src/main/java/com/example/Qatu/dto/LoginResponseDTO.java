package com.example.Qatu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private Integer id;
    private String nombre;
    private String email;
    private String rol;  // VENDEDOR | USUARIO_OBSERVADOR | ADMIN
}
