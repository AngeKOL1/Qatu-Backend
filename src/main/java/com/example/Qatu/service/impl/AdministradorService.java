package com.example.Qatu.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.Qatu.dto.PaginaResponseDTO;
import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.ReporteMapper;
import com.example.Qatu.mapper.VendedorMapper;
import com.example.Qatu.models.Administrador;
import com.example.Qatu.models.Reporte;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.AdministradorRepo;
import com.example.Qatu.repository.ReporteRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.IAdministradorService;
import com.example.Qatu.util.PaginacionUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdministradorService extends GenericService<Administrador, Integer> implements IAdministradorService {
    private final AdministradorRepo repo;
    private final VendedorRepo vendedorRepo;
    private final VendedorMapper vendedorMapper;
    private final WebSocketService webSocketService;
    private final ReporteMapper reporteMapper;
    private final ReporteRepo reporteRepo;

    @Override
    protected AdministradorRepo getRepo() {
        return repo;
    }

    @Override
    public PaginaResponseDTO<VendedorResponseDTO> listarVendedores(
            EstadoVendedor estado, int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio,
                Sort.by("createdAt").descending());

        Page<Vendedor> page = estado != null
                ? vendedorRepo.findByEstado(estado, pageable)
                : vendedorRepo.findAll(pageable);

        return PaginacionUtils.construir(
                page.map(vendedorMapper::toResponseDTO));
    }

    @Override
    @Transactional
    public VendedorResponseDTO cambiarEstadoVendedor(Integer vendedorId, EstadoVendedor nuevoEstado) {
        // Validar que el vendedor exista
        Vendedor vendedor = vendedorRepo.findById(vendedorId)
                .orElseThrow(() -> new ModelNotFoundException("Vendedor no encontrado"));

        EstadoVendedor estadoActual = vendedor.getEstado();

        // Validar que el nuevo estado sea diferente al actual

        if (estadoActual == EstadoVendedor.PENDIENTE && nuevoEstado == EstadoVendedor.SUSPENDIDO) {
            throw new IllegalArgumentException("No se puede cambiar de PENDIENTE a SUSPENDIDO");
        }

        vendedor.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoVendedor.SUSPENDIDO) {
            vendedor.setVisible(false);
            webSocketService.emitirVendedorInactivo(vendedorId);
        }

        Vendedor actualizado = vendedorRepo.save(vendedor);
        return vendedorMapper.toResponseDTO(actualizado);
    }

    @Override
    public PaginaResponseDTO<ReporteResponseDTO> listarReportes(
            String estado, int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio,
                Sort.by("createdAt").descending());

        Page<Reporte> page = estado != null
                ? reporteRepo.findByEstado(estado, pageable)
                : reporteRepo.findAll(pageable);

        return PaginacionUtils.construir(
                page.map(reporteMapper::toResponseDTO));
    }

    @Override
    @Transactional
    public ReporteResponseDTO actualizarReporte(Integer reporteId, String estado, String respuesta) {
        Reporte reporte = reporteRepo.findById(reporteId)
                .orElseThrow(() -> new ModelNotFoundException("Reporte no encontrado"));

        reporte.setEstado(estado);
        if (respuesta != null && !respuesta.isBlank()) {
            reporte.setRespuesta(respuesta);
        }
        return reporteMapper.toResponseDTO(reporteRepo.save(reporte));
    }
}
