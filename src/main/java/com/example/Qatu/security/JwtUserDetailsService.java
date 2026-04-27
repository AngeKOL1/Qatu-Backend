package com.example.Qatu.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.repository.UsuarioObservadorRepo;
import com.example.Qatu.repository.AdministradorRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final VendedorRepo vendedorRepo;
    private final UsuarioObservadorRepo observadorRepo;
    private final AdministradorRepo adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Buscar en vendedores
        var vendedor = vendedorRepo.findByEmail(email);
        if (vendedor.isPresent()) {
            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("VENDEDOR")
            );
            return new CustomUserDetails(
                vendedor.get().getId(),
                vendedor.get().getEmail(),
                vendedor.get().getPassword(),
                vendedor.get().getNombre(),
                "VENDEDOR",
                authorities
            );
        }

        // 2. Buscar en usuarios observadores
        var observador = observadorRepo.findByEmail(email);
        if (observador.isPresent()) {
            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("USUARIO_OBSERVADOR")
            );
            return new CustomUserDetails(
                observador.get().getId(),
                observador.get().getEmail(),
                observador.get().getPassword(),
                observador.get().getNombre(),
                "USUARIO_OBSERVADOR",
                authorities
            );
        }

        // 3. Buscar en administradores
        var admin = adminRepo.findByEmail(email);
        if (admin.isPresent()) {
            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ADMIN")
            );
            return new CustomUserDetails(
                admin.get().getId(),
                admin.get().getEmail(),
                admin.get().getPassword(),
                admin.get().getNombre(),
                "ADMIN",
                authorities
            );
        }

        // 4. No encontrado en ninguna tabla
        throw new UsernameNotFoundException("Usuario no encontrado: " + email);
    }
}