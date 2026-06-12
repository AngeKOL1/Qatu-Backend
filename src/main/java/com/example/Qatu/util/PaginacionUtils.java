package com.example.Qatu.util;

import org.springframework.data.domain.Page;

import com.example.Qatu.dto.PaginaResponseDTO;

// PaginacionUtils.java
public class PaginacionUtils {

    public static <T> PaginaResponseDTO<T> construir(Page<T> page) {
        return PaginaResponseDTO.<T>builder()
            .contenido(page.getContent())
            .paginaActual(page.getNumber())
            .totalPaginas(page.getTotalPages())
            .totalElementos(page.getTotalElements())
            .tamanioPagina(page.getSize())
            .esUltima(page.isLast())
            .esPrimera(page.isFirst())
            .build();
    }
}