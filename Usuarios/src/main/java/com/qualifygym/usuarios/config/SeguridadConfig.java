package com.qualifygym.usuarios.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SeguridadConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Permitir todos los orígenes
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false); // No permitir credenciales para endpoints públicos
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permitir peticiones OPTIONS (preflight de CORS) sin autenticación
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Público - endpoints para login y registro (sin autenticación)
                // Es importante que estos estén antes de otras reglas
                .requestMatchers(HttpMethod.POST, "/api/v1/usuario/login", 
                                 "/api/v1/usuario/register").permitAll()
                
                // Público - GET para comunicación entre microservicios (solo lectura)
                .requestMatchers(HttpMethod.GET, "/api/v1/usuario/users", "/api/v1/usuario/users/**").permitAll()
                
                // Administrador - creación, actualización y eliminación de usuarios
                .requestMatchers(HttpMethod.POST, "/api/v1/usuario/users")
                    .hasAuthority("Administrador")
                .requestMatchers(HttpMethod.PUT, "/api/v1/usuario/users/**")
                    .hasAuthority("Administrador")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/usuario/users/**")
                    .hasAuthority("Administrador")
                
                // Swagger público (sin autenticación)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html")
                    .permitAll()
                
                // Resto de endpoints requieren autenticación
                .anyRequest().authenticated()
            )
            // Configurar HTTP Basic solo para endpoints que requieren autenticación
            .httpBasic(httpBasic -> httpBasic
                .realmName("QualifyGym API")
            )
            .userDetailsService(customUserDetailsService);
        
        return http.build();
    }
}

