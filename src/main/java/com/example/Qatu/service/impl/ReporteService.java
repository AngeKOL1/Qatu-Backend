package com.example.Qatu.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Qatu.dto.ReporteRequestDTO;
import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.ReporteMapper;
import com.example.Qatu.models.Reporte;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.ReporteRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.IReporteService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReporteService extends GenericService<Reporte, Integer> implements IReporteService {
    private final ReporteRepo repo;
    private final VendedorRepo vendedorRepo;
    private final ReporteMapper reporteMapper;

    @Override
    protected ReporteRepo getRepo() {
        return repo;
    }

    @Override
    @Transactional
    public ReporteResponseDTO crearReporte(Integer vendedorId, ReporteRequestDTO dto) {

        Vendedor vendedor = vendedorRepo.findById(vendedorId)
                .orElseThrow(() -> new ModelNotFoundException("Vendedor no encontrado"));

        // Solo vendedores ACTIVOS pueden crear reportes
        if (vendedor.getEstado() != EstadoVendedor.ACTIVO) {
            throw new IllegalArgumentException(
                    "Tu cuenta debe estar activa para enviar reportes");
        }

        Reporte reporte = new Reporte();
        reporte.setAsunto(dto.getAsunto());
        reporte.setDescripcion(dto.getDescripcion());
        reporte.setVendedor(vendedor);
        // estado, createdAt y updatedAt los inicializa @PrePersist

        return reporteMapper.toResponseDTO(repo.save(reporte));
    }

    @Override
    // Ver mis reportes
    public List<ReporteResponseDTO> listarMisReportes(Integer vendedorId) {
        return repo.findByVendedorId(vendedorId)
                .stream()
                .map(reporteMapper::toResponseDTO)
                .toList();
    }

}
