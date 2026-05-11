package com.example.Qatu.service;

import com.example.Qatu.dto.CongestionEventDTO;
import com.example.Qatu.dto.UbicacionEventDTO;
import com.example.Qatu.dto.ZonaResponseDTO;

public interface IWebSoketSevice {
    void emitirUbicacionActualizada(UbicacionEventDTO evento);
    void emitirCongestion(CongestionEventDTO evento);
    void emitirVendedorInactivo(Integer vendedorId);
    void emitirZonaCreada(ZonaResponseDTO zona);
}
