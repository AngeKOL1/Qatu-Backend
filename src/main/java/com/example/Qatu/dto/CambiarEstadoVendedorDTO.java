package com.example.Qatu.dto;

import com.example.Qatu.models.enums.EstadoVendedor;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarEstadoVendedorDTO {
    @NotNull(message = "El nuevo estado del vendedor no puede ser nulo")
    private EstadoVendedor nuevoEstado;
}
