package com.example.Qatu.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Qatu.dto.VendedorRegisterDTO;
import com.example.Qatu.mapper.VendedorMapper;
import com.example.Qatu.models.Categoria;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.repository.CategoriaRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.IVendedorService;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class VendedorService extends GenericService<Vendedor, Integer> implements IVendedorService {
    private final VendedorMapper mapper;
    private final VendedorRepo repo;
    private final CategoriaRepo categoriaRepo;
    private final PasswordEncoder passwordEncoder;
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
}
