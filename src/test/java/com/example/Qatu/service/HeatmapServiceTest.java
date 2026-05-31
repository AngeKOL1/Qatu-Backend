package com.example.Qatu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Qatu.config.UmbralConfig;
import com.example.Qatu.dto.HeatmapPuntoDTO;
import com.example.Qatu.dto.HeatmapResponseDTO;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.service.impl.HeatmapService;

@ExtendWith(MockitoExtension.class)
class HeatmapServiceTest {

    @Mock
    private UbicacionRepo ubicacionRepo;
    @Mock
    private UmbralConfig umbralConfig;

    @InjectMocks
    private HeatmapService heatmapService;

    // ══ calcularHeatmap ═══════════════════════════════════════════════════════

    @Test
    @DisplayName("Calcula heatmap correctamente con puntos activos")
    void calcularHeatmap_exitoso() {
        when(umbralConfig.getRadioMetros()).thenReturn(100);
        when(umbralConfig.getUmbralRojo()).thenReturn(10);
        when(umbralConfig.getUmbralAmarillo()).thenReturn(5);

        Object[] punto1 = { -7.1638, -78.5001, 12 };
        Object[] punto2 = { -7.1645, -78.5010, 7 };
        Object[] punto3 = { -7.1650, -78.5020, 3 };

        List<Object[]> rawData = new ArrayList<>();
        rawData.add(punto1);
        rawData.add(punto2);
        rawData.add(punto3);

        when(ubicacionRepo.findPuntosActivosConDensidad(100)).thenReturn(rawData);

        HeatmapResponseDTO resultado = heatmapService.calcularHeatmap();

        assertNotNull(resultado);
        assertEquals(3, resultado.getPuntos().size());
        assertEquals(10, resultado.getUmbralRojo());
        assertEquals(5, resultado.getUmbralAmarillo());
        assertNotNull(resultado.getCalculadoEn());

        assertEquals("ROJO", resultado.getPuntos().get(0).getNivel());
        assertEquals("AMARILLO", resultado.getPuntos().get(1).getNivel());
        assertEquals("VERDE", resultado.getPuntos().get(2).getNivel());
    }

    @Test
    @DisplayName("Devuelve lista vacía si no hay vendedores activos")
    void calcularHeatmap_sinVendedores() {
        when(umbralConfig.getRadioMetros()).thenReturn(100);
        when(umbralConfig.getUmbralRojo()).thenReturn(10);
        when(umbralConfig.getUmbralAmarillo()).thenReturn(5);
        when(ubicacionRepo.findPuntosActivosConDensidad(100)).thenReturn(List.of());

        HeatmapResponseDTO resultado = heatmapService.calcularHeatmap();

        assertNotNull(resultado);
        assertTrue(resultado.getPuntos().isEmpty());
    }

    @Test
    @DisplayName("Mapea correctamente lat, lng y count de cada punto")
    void calcularHeatmap_mapeoCorrectoDeAtributos() {
        when(umbralConfig.getRadioMetros()).thenReturn(100);
        when(umbralConfig.getUmbralRojo()).thenReturn(10);
        when(umbralConfig.getUmbralAmarillo()).thenReturn(5);

        Object[] punto = { -7.1638, -78.5001, 6 };
        List<Object[]> rawData = new ArrayList<>();
        rawData.add(punto);

        when(ubicacionRepo.findPuntosActivosConDensidad(100)).thenReturn(rawData);

        HeatmapResponseDTO resultado = heatmapService.calcularHeatmap();

        HeatmapPuntoDTO p = resultado.getPuntos().get(0);
        assertEquals(-7.1638, p.getLat(), 0.0001);
        assertEquals(-78.5001, p.getLng(), 0.0001);
        assertEquals(6, p.getVendedoresCount());
        assertEquals("AMARILLO", p.getNivel());
    }

    @Test
    @DisplayName("Incluye los umbrales actuales en la respuesta")
    void calcularHeatmap_incluyeUmbralesEnRespuesta() {
        when(umbralConfig.getRadioMetros()).thenReturn(100);
        when(umbralConfig.getUmbralRojo()).thenReturn(10);
        when(umbralConfig.getUmbralAmarillo()).thenReturn(5);
        when(ubicacionRepo.findPuntosActivosConDensidad(100)).thenReturn(List.of());

        HeatmapResponseDTO resultado = heatmapService.calcularHeatmap();

        assertEquals(10, resultado.getUmbralRojo());
        assertEquals(5, resultado.getUmbralAmarillo());
    }

    // ══ determinarNivel ═══════════════════════════════════════════════════════

    @Test
    @DisplayName("Devuelve ROJO cuando count supera el umbral rojo")
    void determinarNivel_rojo() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);

        assertEquals("ROJO", heatmapService.determinarNivel(10));
        assertEquals("ROJO", heatmapService.determinarNivel(15));
        assertEquals("ROJO", heatmapService.determinarNivel(100));
    }

    @Test
    @DisplayName("Devuelve AMARILLO cuando count está entre umbrales")
    void determinarNivel_amarillo() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);
        when(umbralConfig.getUmbralAmarillo()).thenReturn(5);

        assertEquals("AMARILLO", heatmapService.determinarNivel(5));
        assertEquals("AMARILLO", heatmapService.determinarNivel(7));
        assertEquals("AMARILLO", heatmapService.determinarNivel(9));
    }

    @Test
    @DisplayName("Devuelve VERDE cuando count está por debajo del umbral amarillo")
    void determinarNivel_verde() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);
        when(umbralConfig.getUmbralAmarillo()).thenReturn(5);

        assertEquals("VERDE", heatmapService.determinarNivel(0));
        assertEquals("VERDE", heatmapService.determinarNivel(1));
        assertEquals("VERDE", heatmapService.determinarNivel(4));
    }

    @Test
    @DisplayName("Exactamente en el umbral rojo devuelve ROJO")
    void determinarNivel_exactamenteEnUmbralRojo() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);

        assertEquals("ROJO", heatmapService.determinarNivel(10));
    }

    @Test
    @DisplayName("Exactamente en el umbral amarillo devuelve AMARILLO")
    void determinarNivel_exactamenteEnUmbralAmarillo() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);
        when(umbralConfig.getUmbralAmarillo()).thenReturn(5);

        assertEquals("AMARILLO", heatmapService.determinarNivel(5));
    }

    // ══ actualizarUmbral ══════════════════════════════════════════════════════

    @Test
    @DisplayName("Actualiza umbrales correctamente")
    void actualizarUmbral_exitoso() {
        heatmapService.actualizarUmbral(15, 8);

        verify(umbralConfig, times(1)).setUmbralRojo(15);
        verify(umbralConfig, times(1)).setUmbralAmarillo(8);
    }

    @Test
    @DisplayName("Lanza excepción si umbral amarillo >= umbral rojo")
    void actualizarUmbral_amarilloMayorQueRojo() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> heatmapService.actualizarUmbral(5, 10));

        assertEquals("El umbral amarillo debe ser menor que el umbral rojo",
                ex.getMessage());
        verify(umbralConfig, never()).setUmbralRojo(anyInt());
        verify(umbralConfig, never()).setUmbralAmarillo(anyInt());
    }

    @Test
    @DisplayName("Lanza excepción si ambos umbrales son iguales")
    void actualizarUmbral_iguales() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> heatmapService.actualizarUmbral(10, 10));

        assertEquals("El umbral amarillo debe ser menor que el umbral rojo",
                ex.getMessage());
        verify(umbralConfig, never()).setUmbralRojo(anyInt());
        verify(umbralConfig, never()).setUmbralAmarillo(anyInt());
    }

    @Test
    @DisplayName("No actualiza umbrales si la validación falla")
    void actualizarUmbral_noActualizaSiFalla() {
        assertThrows(
                IllegalArgumentException.class,
                () -> heatmapService.actualizarUmbral(3, 8));

        verify(umbralConfig, never()).setUmbralRojo(anyInt());
        verify(umbralConfig, never()).setUmbralAmarillo(anyInt());
    }
}