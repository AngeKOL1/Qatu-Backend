package com.example.Qatu.job;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.IWebSoketSevice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InactividadVendedorJob {

    private final UbicacionRepo ubicacionRepo;
    private final VendedorRepo vendedorRepo;
    private final IWebSoketSevice webSoketSevice;

    // Se ejecuta cada 5 minutos
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void marcarVendedoresInactivos() {

        LocalDateTime limite = LocalDateTime.now().minusMinutes(10);

        Page<Vendedor> vendedoresActivos = vendedorRepo
            .findByEstado(EstadoVendedor.ACTIVO, Pageable.unpaged());

        vendedoresActivos.forEach(vendedor -> {
            ubicacionRepo.findByVendedorIdAndActivoTrue(vendedor.getId())
                .ifPresent(ubicacion -> {
                    if (ubicacion.getTimestamp().isBefore(limite)) {
                        // Desactivar ubicación
                        ubicacion.setActivo(false);
                        ubicacionRepo.save(ubicacion);

                        webSoketSevice.emitirVendedorInactivo(vendedor.getId());

                        log.info("Vendedor {} marcado inactivo por falta de GPS", 
                            vendedor.getId());
                    }
                });
        });
    }
}