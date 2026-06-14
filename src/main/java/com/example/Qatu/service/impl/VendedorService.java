package com.example.Qatu.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Qatu.dto.PaginaResponseDTO;
import com.example.Qatu.dto.VendedorMapaDTO;
import com.example.Qatu.dto.VendedorPerfilDTO;
import com.example.Qatu.dto.VendedorRegisterDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.ProductoMapper;
import com.example.Qatu.mapper.VendedorMapper;
import com.example.Qatu.models.Categoria;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.CategoriaRepo;
import com.example.Qatu.repository.ProductoRepo;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.IVendedorService;
import com.example.Qatu.util.GeoUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VendedorService extends GenericService<Vendedor, Integer> implements IVendedorService {
    private final VendedorMapper mapper;
    private final VendedorRepo repo;
    private final CategoriaRepo categoriaRepo;
    private final PasswordEncoder passwordEncoder;
    private final UbicacionRepo ubicacionRepo;
    private final ProductoRepo productoRepo;
    private final ProductoMapper productoMapper;

    @Override
    protected VendedorRepo getRepo() {
        return repo;
    }

    // Logica para registrar un nuevo vendedor
    @Override
    public Vendedor registrarVendedor(VendedorRegisterDTO dto) {
        // 1. Validar email único
        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // 2. Buscar la categoría
        Categoria categoria = categoriaRepo.findByNombre(dto.getNombreCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        // 3. Mapear el DTO a entidad
        Vendedor vendedor = mapper.toVendedor(dto);

        // 4. Asignar datos que no vienen del DTO
        vendedor.setCategoria(categoria);
        vendedor.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 5. Guardar
        return repo.save(vendedor);
    }

    @Override
    @Transactional
    public VendedorResponseDTO cambiarVisibilidad(Integer vendedorId, Boolean visible) {

        Vendedor vendedor = repo.findById(vendedorId)
                .orElseThrow(() -> new ModelNotFoundException("Vendedor no encontrado"));

        // Solo un vendedor ACTIVO puede cambiar su visibilidad
        if (vendedor.getEstado() != EstadoVendedor.ACTIVO) {
            throw new IllegalArgumentException(
                    "Tu cuenta aún no está aprobada. No puedes aparecer en el mapa.");
        }

        vendedor.setVisible(visible);
        Vendedor actualizado = repo.save(vendedor);

        return mapper.toResponseDTO(actualizado);
    }

    @Override
    public void actualizarFcmToken(Integer vendedorId, String fcmToken) {
        Vendedor vendedor = repo.findById(vendedorId)
                .orElseThrow(() -> new ModelNotFoundException("Vendedor no encontrado"));
        vendedor.setFcmToken(fcmToken);
        repo.save(vendedor);
    }

    @Override
    public PaginaResponseDTO<VendedorMapaDTO> listarVendedoresActivosEnMapa(
            String categoria, int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio);

        Page<Vendedor> page = repo
                .findByEstadoAndVisibleTrue(EstadoVendedor.ACTIVO, pageable);

        // Filtrar por categoría si se especifica
        List<VendedorMapaDTO> contenido = page.getContent().stream()
                .filter(v -> categoria == null || categoria.isBlank() ||
                        (v.getCategoria() != null &&
                                v.getCategoria().getNombre().equalsIgnoreCase(categoria)))
                .map(this::toMapaDTOConUbicacion)
                .toList();

        return PaginaResponseDTO.<VendedorMapaDTO>builder()
                .contenido(contenido)
                .paginaActual(page.getNumber())
                .totalPaginas(page.getTotalPages())
                .totalElementos(page.getTotalElements())
                .tamanioPagina(page.getSize())
                .esUltima(page.isLast())
                .esPrimera(page.isFirst())
                .build();
    }

    @Override
    public VendedorPerfilDTO obtenerPerfil(Integer vendedorId, int pagina, int tamanio) {
        Vendedor vendedor = repo.findById(vendedorId)
                .orElseThrow(() -> new ModelNotFoundException("Vendedor no encontrado"));

        return toPerfilDTOCompleto(vendedor, pagina, tamanio);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private VendedorMapaDTO toMapaDTOConUbicacion(Vendedor v) {
        VendedorMapaDTO dto = mapper.toMapaDTO(v);

        ubicacionRepo.findByVendedorIdAndActivoTrue(v.getId())
                .ifPresent(u -> {
                    dto.setLat(GeoUtils.getLat(u.getCoordenada()));
                    dto.setLng(GeoUtils.getLng(u.getCoordenada()));
                });

        return dto;
    }

    private VendedorPerfilDTO toPerfilDTOCompleto(Vendedor v, int pagina, int tamanio) {
        VendedorPerfilDTO dto = mapper.toPerfilDTO(v);

        ubicacionRepo.findByVendedorIdAndActivoTrue(v.getId())
                .ifPresent(u -> {
                    dto.setLat(GeoUtils.getLat(u.getCoordenada()));
                    dto.setLng(GeoUtils.getLng(u.getCoordenada()));
                });

        return dto;
    }
}
