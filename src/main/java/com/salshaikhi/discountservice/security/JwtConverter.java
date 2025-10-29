package com.salshaikhi.discountservice.security;


import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    @Value("${keycloak.client-id}")
    private String clientId;
    private static final String RESOURCE_ACCESS = "resource_access";

    @Override
    public AbstractAuthenticationToken convert(@Nullable Jwt jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("JWT cannot be null");
        }
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractClientRoles(jwt).stream()).collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaim(JwtClaimNames.SUB));
    }

    private Collection<? extends GrantedAuthority> extractClientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess == null) {
            return Set.of();
        }
        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(clientId);
        if (resource == null) {
            return Set.of();
        }
        Collection<String> clientRoles = (Collection<String>) resource.get("roles");
        if (clientRoles == null || clientRoles.isEmpty()) {
            return Set.of();
        }
        return rolesMapping(clientRoles);
    }

    private Set<SimpleGrantedAuthority> rolesMapping(Collection<String> rolesSet) {
        return rolesSet.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}