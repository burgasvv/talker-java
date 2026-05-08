package org.burgas.talkerjava.security;

import lombok.RequiredArgsConstructor;
import org.burgas.talkerjava.service.IdentityDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final IdentityDetailsService identityDetailsService;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(identityDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity
                .cors(cors -> cors.configurationSource(new UrlBasedCorsConfigurationSource()))
                .csrf(csrf -> csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()))
                .httpBasic(httpBasic -> httpBasic
                        .securityContextRepository(new RequestAttributeSecurityContextRepository()))
                .authenticationManager(authenticationManager())
                .authorizeHttpRequests(
                        authorize -> authorize

                                .requestMatchers(
                                        "/api/v1/security/csrf-token",

                                        "/api/v1/identities/by-id", "/api/v1/identities/create",

                                        "/api/v1/identity-images/by-id",

                                        "/api/v1/chats/by-id",

                                        "/api/v1/messages/ws/by-chat",

                                        "/api/v1/communities/by-id",

                                        "/api/v1/publications/ws/by-community"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/api/v1/identities/update", "/api/v1/identities/delete",

                                        "/api/v1/identity-images/create", "/api/v1/identity-images/delete",
                                        "/api/v1/identity-images/make-preview",

                                        "/api/v1/chats/create", "/api/v1/chats/update", "/api/v1/chats/delete",
                                        "/api/v1/chats/join", "/api/v1/chats/out",

                                        "/api/v1/messages/by-id", "/api/v1/messages/create", "/api/v1/messages/delete",

                                        "/api/v1/communities/create", "/api/v1/communities/update",
                                        "/api/v1/communities/delete", "/api/v1/communities/join", "/api/v1/communities/out",

                                        "/api/v1/publications/by-id", "/api/v1/publications/create", "/api/v1/publications/delete"
                                )
                                .hasAnyAuthority("ADMIN", "USER")

                                .requestMatchers(
                                        "/api/v1/identities",

                                        "/api/v1/chats",

                                        "/api/v1/communities"
                                )
                                .hasAnyAuthority("ADMIN")
                )
                .build();
    }
}
