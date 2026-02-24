package com.bankingplatform.api_gatawey.filter;



import com.bankingplatform.api_gatawey.security.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationFilter implements WebFilter {

    @Autowired
    private Jwt jwt;


// rotas públicas que não precisam de autenticação
    private static final List<String> PUBLIC_ROUTES = List.of(
            "/auth/login",
            "/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {


        String path = exchange.getRequest().getURI().getPath();

        if (Public(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.Error(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        if (!jwt.isTokenValid(token)) {
            return this.Error(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
        }
        DecodedJWT decoded = jwt.decodeToken(token);

        String username = decoded.getSubject();
        var roles = decoded.getClaim("roles").asList(String.class);


        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );
        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private boolean Public(String path) {
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> Error(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}
