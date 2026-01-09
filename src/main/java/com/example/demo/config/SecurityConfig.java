package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.Services.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers(
                                "/index.html",
                                "/login.html",
                                "/api/users/login",
                                "/api/users/register",
                                "/api/users/me",
                                "/ws/**", // WebSocket
                                "/css/**", // Estilos y scripts estáticos si los tienes
                                "/js/**",
                                "/Imagen/**"
                        ).permitAll()
                        // Endpoints de chat
                        .requestMatchers("/api/chat/**").authenticated()
                        // Accesos por roles
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/alumnos/**", "/api/matricula/**", "/api/gestion-pagos/**", "/api/pagos/realizar", "/api/anulacion/**").hasAnyRole("SECRETARIA", "ADMIN")
                        .requestMatchers("/api/director/**", "/api/2fa/**", "/api/reportes/**", "/api/anulacion/**").hasAnyRole("DIRECTOR", "ADMIN")
                        // Resto bloqueado
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/index.html")
                        .loginProcessingUrl("/api/users/login")
                        .defaultSuccessUrl("/principal.html", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")
                        .logoutSuccessUrl("/index.html")
                        .permitAll()
                );

        return http.build();
    }
}