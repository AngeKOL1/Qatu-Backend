package com.example.Qatu.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdministradorService extends GenericService<Administrador, Integer> implements IAdministradorService{
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
    public List<VendedorResponseDTO> listarVendedores(EstadoVendedor estado) {
        List<Vendedor> vendedores = estado != null
            ? vendedorRepo.findByEstado(estado)
            : vendedorRepo.findAll();

        return vendedores.stream()
            .map(vendedorMapper::toResponseDTO)
            .toList();
    }

    @Override
    @Transactional
    public VendedorResponseDTO cambiarEstadoVendedor(Integer vendedorId, EstadoVendedor nuevoEstado){
        // Validar que el vendedor exista
        Vendedor vendedor = vendedorRepo.findById(vendedorId)
        .orElseThrow(()-> new ModelNotFoundException("Vendedor no encontrado"));

        EstadoVendedor estadoActual= vendedor.getEstado();
         
        // Validar que el nuevo estado sea diferente al actual

        if(estadoActual == EstadoVendedor.PENDIENTE && nuevoEstado == EstadoVendedor.SUSPENDIDO){
            throw new IllegalArgumentException("No se puede cambiar de PENDIENTE a SUSPENDIDO");
        }
         
        vendedor.setEstado(nuevoEstado);

        if(nuevoEstado == EstadoVendedor.SUSPENDIDO){
            vendedor.setVisible(false);
            webSocketService.emitirVendedorInactivo(vendedorId);
        } 

        Vendedor actualizado = vendedorRepo.save(vendedor);
        return vendedorMapper.toResponseDTO(actualizado);
    }
    @Override
    public List<ReporteResponseDTO> listarReportes(String estado){
        List<Reporte> reportes = estado != null
            ? reporteRepo.findByEstado(estado)
            : reporteRepo.findAll();

        return reportes.stream()
            .map(reporteMapper::toResponseDTO)
            .toList();
    }
    @Override
    @Transactional
    public ReporteResponseDTO actualizarReporte(Integer reporteId, String estado, String respuesta){
        Reporte reporte = reporteRepo.findById(reporteId)
            .orElseThrow(() -> new ModelNotFoundException("Reporte no encontrado"));
        
        reporte.setEstado(estado);
        if(respuesta != null && !respuesta.isBlank()){
            reporte.setRespuesta(respuesta);
        }
        return reporteMapper.toResponseDTO(reporteRepo.save(reporte));
    }
}
