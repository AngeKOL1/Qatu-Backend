// package com.example.restapp.GestorFinanciero.Security;

// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.example.restapp.GestorFinanciero.models.Usuario;
// import com.example.restapp.GestorFinanciero.repo.UsuarioRepo;
// import com.example.restapp.GestorFinanciero.service.IUsuarioService;

// import lombok.RequiredArgsConstructor;


// @Service
// @RequiredArgsConstructor
// public class CustomUserDetailsService implements UserDetailsService {
    
//     private final IUsuarioService usuarioService;
//     private final UsuarioRepo
    
//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         Usuario usuario = usuarioService.findBy(username);
        
//         if (usuario == null) {
//             throw new UsernameNotFoundException("Usuario no encontrado");
//         }
        
//         return new CustomUserDetails(
//             usuario.getId(),
//             usuario.getUsername(),
//             usuario.getPassword(),
//             usuario.getNombre(),
//             Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
//         );
//     }
// }