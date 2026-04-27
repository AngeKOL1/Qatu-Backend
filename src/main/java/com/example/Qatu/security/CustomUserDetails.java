package com.example.Qatu.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final Integer id;
    private final String nombre;
    private final String rol; // VENDEDOR | USUARIO_OBSERVADOR | ADMIN

    public CustomUserDetails(Integer id, String username, String password,
                             String nombre, String rol,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }
}