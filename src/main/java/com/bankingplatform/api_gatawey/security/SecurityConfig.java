package com.bankingplatform.api_gatawey.security;

import com.bankingplatform.api_gatawey.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterBefore(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange

                        .pathMatchers("/auth/**").permitAll()

                        .pathMatchers(HttpMethod.GET, "/accounts/**")
                        .hasRole("USER")

                        .pathMatchers(HttpMethod.POST, "/transfers/**")
                        .hasRole("USER")

                        .pathMatchers("/payments/**")
                        .hasAnyRole("MANAGER", "ADMIN")

                        .pathMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .anyExchange().authenticated()
                )
                .build();
    }
}
//Configura as regras de segurança do Gateway
//Define
//Quais rotas são públicas (/auth/**)
//Quais rotas requerem roles específicas /accounts/** para USER, /admin/** para ADMIN

