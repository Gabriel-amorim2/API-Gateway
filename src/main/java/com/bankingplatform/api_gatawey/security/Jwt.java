    package com.bankingplatform.api_gatawey.security;


    import com.auth0.jwt.JWT;
    import com.auth0.jwt.algorithms.Algorithm;
    import com.auth0.jwt.interfaces.DecodedJWT;
    import jakarta.annotation.PostConstruct;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Component;


    import java.util.List;

    @Component
    public class Jwt {

        private Algorithm algorithm;

        @Value("${api.security.token}")
        private String secret;

        @PostConstruct
        public void init() {
            this.algorithm = Algorithm.HMAC256(secret);
        }

        public boolean isTokenValid(String token) {
            try {
                JWT.require(algorithm).build().verify(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public DecodedJWT decodeToken(String token) {
            return JWT.require(algorithm).build().verify(token);
        }

        public List<String> getRoles(String token) {
            return decodeToken(token).getClaim("roles").asList(String.class);
        }
    }
