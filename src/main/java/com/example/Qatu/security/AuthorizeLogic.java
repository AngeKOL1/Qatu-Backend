package com.example.Qatu.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthorizeLogic {

    public boolean hasAccess(String path) {
        boolean result = false;

        String methodRole = switch (path) {
            case "findAll"              -> "ADMIN";
            case "findById", "getById" -> "VENDEDOR,USUARIO_OBSERVADOR,ADMIN";
            case "adminOnly"           -> "ADMIN";
            case "vendedorOnly"        -> "VENDEDOR";
            case "observadorOnly"      -> "USUARIO_OBSERVADOR";
            default                    -> "ADMIN";
        };

        String[] methodRoles = methodRole.split(",");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", auth.getName());

        for (GrantedAuthority ga : auth.getAuthorities()) {
            String roleUser = ga.getAuthority();
            log.info("Role: {}", roleUser);

            for (String role : methodRoles) {
                if (roleUser.equalsIgnoreCase(role)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }
}