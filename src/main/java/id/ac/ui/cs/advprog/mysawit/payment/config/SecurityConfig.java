package id.ac.ui.cs.advprog.mysawit.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // untuk testing lokal
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/payroll/**").permitAll()   // izinkan semua request ke /api/payroll/*
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}