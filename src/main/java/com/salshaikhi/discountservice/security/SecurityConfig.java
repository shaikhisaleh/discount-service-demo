package com.salshaikhi.discountservice.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Value("${keycloak.issuer-uri}")
    private String jwtIssuerUri;
    private final JwtConverter jwtConverter;

    public SecurityConfig(JwtConverter jwtConverter) {
        this.jwtConverter = jwtConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        //Whitelist swagger ui for development
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/v3/api-docs.yaml", "/actuator/**").permitAll()
                        .anyRequest().authenticated());
        http.oauth2ResourceServer(
                oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(JwtDecoders.fromOidcIssuerLocation(jwtIssuerUri)))
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));
        return http.build();
    }

}

