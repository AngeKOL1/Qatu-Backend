package com.example.Qatu.job;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InactividadVendedorJob {

    private final UbicacionRepo ubicacionRepo;
    private final VendedorRepo vendedorRepo;

    // Se ejecuta cada 5 minutos
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void marcarVendedoresInactivos() {

        LocalDateTime limite = LocalDateTime.now().minusMinutes(10);

        List<Vendedor> vendedoresActivos = vendedorRepo
            .findByEstado(EstadoVendedor.ACTIVO);

        for (Vendedor vendedor : vendedoresActivos) {
            ubicacionRepo.findByVendedorIdAndActivaTrue(vendedor.getId())
                .ifPresent(ubicacion -> {
                    if (ubicacion.getTimestamp().isBefore(limite)) {
                        // Desactivar ubicación
                        ubicacion.setActiva(false);
                        ubicacionRepo.save(ubicacion);
                        log.info("Vendedor {} marcado inactivo por falta de GPS", 
                            vendedor.getId());
                    }
                });
        }
    }
}