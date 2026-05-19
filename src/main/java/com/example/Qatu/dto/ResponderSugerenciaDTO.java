package com.example.Qatu.dto;

import com.example.Qatu.models.enums.EstadoSugerencia;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ResponderSugerenciaDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponderSugerenciaDTO {

    @NotNull(message = "La acción es obligatoria")
    private EstadoSugerencia accion; 
}
