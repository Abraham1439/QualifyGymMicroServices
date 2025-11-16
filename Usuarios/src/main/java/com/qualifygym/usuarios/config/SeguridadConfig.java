package com.qualifygym.usuarios.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Público - endpoints para comunicación entre microservicios
                .requestMatchers("/api/v1/usuario/login", 
                                 "/api/v1/usuario/register",
                                 "/api/v1/usuario/users",
                                 "/api/v1/usuario/users/**").permitAll()
                
                // Swagger solo para rol "Administrador"
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**")
                    .hasAuthority("Administrador")
                
                // Resto de endpoints
                .anyRequest().authenticated()
            )
            .userDetailsService(customUserDetailsService)
            .httpBasic(withDefaults());
        
        return http.build();
    }
}

