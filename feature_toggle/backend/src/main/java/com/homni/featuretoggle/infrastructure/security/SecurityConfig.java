package com.homni.featuretoggle.infrastructure.security;

import com.homni.featuretoggle.application.port.in.UserUseCase;
import com.homni.featuretoggle.domain.model.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final ApiKeyAuthFilter apiKeyAuthFilter;
    private final SecurityErrorHandler securityErrorHandler;
    private final UserUseCase userUseCase;
    private final String issuerUri;

    SecurityConfig(ApiKeyAuthFilter apiKeyAuthFilter,
                   SecurityErrorHandler securityErrorHandler,
                   UserUseCase userUseCase,
                   @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}") String issuerUri) {
        this.apiKeyAuthFilter = apiKeyAuthFilter;
        this.securityErrorHandler = securityErrorHandler;
        this.userUseCase = userUseCase;
        this.issuerUri = issuerUri;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/environments/**").hasAnyRole("ADMIN", "EDITOR")
                        .requestMatchers("/api-keys/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/toggles", "/toggles/**").hasAnyRole("ADMIN", "EDITOR", "READER")
                        .requestMatchers(HttpMethod.POST, "/toggles").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/toggles/**").hasAnyRole("ADMIN", "EDITOR")
                        .requestMatchers(HttpMethod.PATCH, "/toggles/**").hasAnyRole("ADMIN", "EDITOR")
                        .requestMatchers(HttpMethod.DELETE, "/toggles/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityErrorHandler)
                        .accessDeniedHandler(securityErrorHandler)
                )
                .addFilterBefore(apiKeyAuthFilter, UsernamePasswordAuthenticationFilter.class);

        if (issuerUri != null && !issuerUri.isBlank()) {
            http.oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        }

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return converter;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        String subject = jwt.getSubject();
        if (subject == null) {
            log.warn("JWT missing 'sub' claim, ensure 'openid' scope is assigned to the client");
            return Collections.emptyList();
        }

        String email = jwt.getClaimAsString("email");
        if (email == null) {
            log.warn("JWT missing 'email' claim for sub={}, ensure 'email' scope is assigned to the client", subject);
            return Collections.emptyList();
        }

        String name = jwt.getClaimAsString("name");
        if (name == null) {
            name = jwt.getClaimAsString("preferred_username");
        }

        AppUser user = userUseCase.findOrCreateByOidcSubject(subject, email, name);

        if (!user.canAuthenticate()) {
            return Collections.emptyList();
        }

        return List.of(new SimpleGrantedAuthority("ROLE_" + user.currentRole().name()));
    }
}
