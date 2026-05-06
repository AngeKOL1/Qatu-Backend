package com.example.Qatu.repository;

import java.util.List;

import com.example.Qatu.models.Reporte;

public interface ReporteRepo extends GenericRepo<Reporte, Integer> { 
    List<Reporte> findByEstado(String estado);
}
