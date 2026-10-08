package br.senai.carteirinha.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationConverter
            jwtAuthenticationConverter
    ) throws Exception {

        http
            .csrf(
                csrf ->
                    csrf.disable()
            )
            .headers(
                headers ->
                    headers.frameOptions(
                        frame ->
                            frame.sameOrigin()
                    )
            )
            .authorizeHttpRequests(
                auth ->
                    auth
                        .requestMatchers(
                            "/auth/login",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**",
                            "/h2-console/**"
                        )
                        .permitAll()

                        .requestMatchers(
                            "/professores/me/**"
                        )
                        .hasRole(
                            "PROFESSOR"
                        )

                        .requestMatchers(
                            "/unidades-curriculares/**"
                        )
                        .hasRole(
                            "ALUNO"
                        )

                        .anyRequest()
                        .denyAll()
            )
            .oauth2ResourceServer(
                oauth2 ->
                    oauth2.jwt(
                        jwt ->
                            jwt.jwtAuthenticationConverter(
                                jwtAuthenticationConverter
                            )
                    )
            );

        return http.build();
    }

    @Bean
    JwtAuthenticationConverter
        jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter
            authoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

        authoritiesConverter
            .setAuthoritiesClaimName(
                "perfil"
            );

        authoritiesConverter
            .setAuthorityPrefix(
                "ROLE_"
            );

        JwtAuthenticationConverter
            authenticationConverter =
            new JwtAuthenticationConverter();

        authenticationConverter
            .setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
            );

        return authenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}