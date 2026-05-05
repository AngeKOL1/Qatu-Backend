package com.example.Qatu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.Qatu.dto.CongestionEventDTO;
import com.example.Qatu.dto.UbicacionEventDTO;
import com.example.Qatu.service.IWebSoketSevice;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService implements IWebSoketSevice{

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    // Emite la nueva posición del vendedor a todos los clientes del mapa
    public void emitirUbicacionActualizada(UbicacionEventDTO evento) {
        log.info("WS → UBICACION_ACTUALIZADA vendedor={} lat={} lng={}",
                evento.getVendedorId(), evento.getLat(), evento.getLng());

        messagingTemplate.convertAndSend("/topic/mapa/ubicaciones", evento);
    }

    // Emite el cambio de nivel de congestión de una zona
    @Override
    public void emitirCongestion(CongestionEventDTO evento) {
        log.info("WS → ZONA_CONGESTIONADA nivel={} lat={} lng={}",
                evento.getNivel(), evento.getLat(), evento.getLng());

        messagingTemplate.convertAndSend("/topic/mapa/congestion", evento);
    }


    // Emite cuando el vendedor se desconecta (pin desaparece del mapa)
    @Override
    public void emitirVendedorInactivo(Integer vendedorId) {
        log.info("WS → VENDEDOR_INACTIVO vendedorId={}", vendedorId);

        UbicacionEventDTO evento = UbicacionEventDTO.builder()
                .evento("VENDEDOR_INACTIVO")
                .vendedorId(vendedorId)
                .visible(false)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/mapa/ubicaciones", evento);
    }
}