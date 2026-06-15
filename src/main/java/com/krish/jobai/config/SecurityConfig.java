package com.krish.jobai.config;

import com.krish.jobai.filter.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // DISABLE CSRF
                .csrf(csrf -> csrf.disable())

                // ENABLE CORS
                .cors(Customizer.withDefaults())

                // STATELESS SESSION
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // AUTHORIZATION
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC APIs
                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login",
                                "/api/resume/**",
                                "/api/ai/**"
                        ).permitAll()

                        // JOB VIEW
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs/**"
                        ).authenticated()

                        // ADMIN ONLY
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/jobs/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/jobs/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/jobs/**"
                        ).hasRole("ADMIN")

                        // EVERYTHING ELSE
                        .anyRequest().authenticated()
                )

                // DISABLE BASIC AUTH POPUP
                .httpBasic(httpBasic -> httpBasic.disable());

        // JWT FILTER
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "https://jobai-frontend-dusky.vercel.app",
                        "https://jobai-frontend-8r3vpzan7-krish-thakur-s-projects.vercel.app"
                )
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}