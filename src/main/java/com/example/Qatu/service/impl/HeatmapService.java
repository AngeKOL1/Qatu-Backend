package com.example.Qatu.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Qatu.config.UmbralConfig;
import com.example.Qatu.dto.HeatmapPuntoDTO;
import com.example.Qatu.dto.HeatmapResponseDTO;
import com.example.Qatu.repository.UbicacionRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeatmapService {

    private final UbicacionRepo ubicacionRepo;
    private final UmbralConfig umbralConfig;

    public HeatmapResponseDTO calcularHeatmap() {

        List<Object[]> rawData = ubicacionRepo
            .findPuntosActivosConDensidad(umbralConfig.getRadioMetros());

        List<HeatmapPuntoDTO> puntos = new ArrayList<>();

        for (Object[] row : rawData) {
            double lat   = ((Number) row[0]).doubleValue();
            double lng   = ((Number) row[1]).doubleValue();
            int    count = ((Number) row[2]).intValue();

            String nivel = determinarNivel(count);

            puntos.add(HeatmapPuntoDTO.builder()
                .lat(lat)
                .lng(lng)
                .vendedoresCount(count)
                .nivel(nivel)
                .build());
        }

        log.info("Heatmap calculado — {} puntos activos", puntos.size());

        return HeatmapResponseDTO.builder()
            .puntos(puntos)
            .umbralRojo(umbralConfig.getUmbralRojo())
            .umbralAmarillo(umbralConfig.getUmbralAmarillo())
            .calculadoEn(LocalDateTime.now())
            .build();
    }

    // Actualizar umbral desde el panel admin
    public void actualizarUmbral(int umbralRojo, int umbralAmarillo) {
        if (umbralAmarillo >= umbralRojo) {
            throw new IllegalArgumentException(
                "El umbral amarillo debe ser menor que el umbral rojo");
        }
        umbralConfig.setUmbralRojo(umbralRojo);
        umbralConfig.setUmbralAmarillo(umbralAmarillo);
        log.info("Umbrales actualizados — rojo={} amarillo={}",
            umbralRojo, umbralAmarillo);
    }

    public String determinarNivel(int count) {
        if (count >= umbralConfig.getUmbralRojo())      return "ROJO";
        if (count >= umbralConfig.getUmbralAmarillo())  return "AMARILLO";
        return "VERDE";
    }
}