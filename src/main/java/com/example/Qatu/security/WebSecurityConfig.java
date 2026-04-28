package com.example.Qatu.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    // Roles
    public static final String ADMIN = "ADMIN";
    public static final String VENDEDOR = "VENDEDOR";
    public static final String USUARIO_OBSERVADOR = "USUARIO_OBSERVADOR";

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",  // panel web React
            "http://localhost:3000"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(req -> req

                // ── Públicas — registro y login ──────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/auth/register/vendedor").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register/observador").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers("/api/vendedores/mi-**").hasAuthority("VENDEDOR")

                // // ── Catálogo público ─────────────────────────────────────
                // .requestMatchers(HttpMethod.GET, "/api/vendedores/{id}/productos").permitAll()
                // .requestMatchers(HttpMethod.GET, "/api/vendedores").permitAll()
                // .requestMatchers(HttpMethod.GET, "/api/vendedores/{id}").permitAll()

                // // ── Vendedor ─────────────────────────────────────────────
                // .requestMatchers(HttpMethod.PATCH, "/api/vendedores/**").hasAuthority("VENDEDOR")
                // .requestMatchers(HttpMethod.POST,  "/api/vendedores/**").hasAuthority("VENDEDOR")
                // .requestMatchers(HttpMethod.PUT,   "/api/vendedores/**").hasAuthority("VENDEDOR")
                // .requestMatchers(HttpMethod.DELETE,"/api/vendedores/**").hasAuthority("VENDEDOR")
                // .requestMatchers(HttpMethod.POST,  "/api/reportes").hasAuthority("VENDEDOR")

                // // ── UsuarioObservador ────────────────────────────────────
                // .requestMatchers(HttpMethod.GET, "/api/mapa/vendedores").hasAuthority("USUARIO_OBSERVADOR")

                // // ── Mapa y heatmap (cualquier autenticado) ───────────────
                // .requestMatchers(HttpMethod.GET, "/api/mapa/**").authenticated()
                // .requestMatchers(HttpMethod.GET, "/api/zonas").authenticated()

                // // ── Admin ────────────────────────────────────────────────
                // .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                // .requestMatchers(HttpMethod.POST,   "/api/zonas").hasAuthority("ADMIN")
                // .requestMatchers(HttpMethod.PUT,    "/api/zonas/**").hasAuthority("ADMIN")
                // .requestMatchers(HttpMethod.DELETE, "/api/zonas/**").hasAuthority("ADMIN")
                // .requestMatchers(HttpMethod.PATCH,  "/api/zonas/**").hasAuthority("ADMIN")

                // ── Todo lo demás requiere autenticación ─────────────────
                .anyRequest().authenticated()
            )
            .exceptionHandling(e ->
                e.authenticationEntryPoint(jwtAuthenticationEntryPoint));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}