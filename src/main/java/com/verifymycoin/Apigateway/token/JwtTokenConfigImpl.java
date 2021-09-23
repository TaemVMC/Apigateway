package com.verifymycoin.Apigateway.token;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenConfigImpl implements JwtTokenConfig{

    @Value("${token.secret}")
    private String secret;
    private final String ISSUER = "VMC";
    private final long JWT_DURATION_MIN = 30;

    private final ReactiveValueOperations<String, String> reactiveValueOperations;

    @Override
    public String makeToken(String userId, String useremail) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .setIssuer(ISSUER) // (2)
                .setIssuedAt(now) // (3)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(JWT_DURATION_MIN).toMillis())) // (4)
                .claim("id", userId) // (5)
                .claim("email", useremail)
                .signWith(SignatureAlgorithm.HS256, secret) // (6)
                .compact();
    }

    @Override
    public Claims parseToken(String jwt) {
        return Jwts.parser()
                .setSigningKey(secret) // (3)
                .parseClaimsJws(jwt) // (4)
                .getBody();
    }

    @Override
    public String resolveToken(ServerHttpRequest request) {
        String authorizationHeader = request.getHeaders().getFirst("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new IllegalArgumentException("Authorization Header Error");
        }
        return authorizationHeader.substring("Bearer ".length());
    }

    @Override
    public Mono<Boolean> saveTokenInRedis(String userId, String jwt) {
        return reactiveValueOperations.set(userId, jwt, Duration.ofMinutes(30));
    }

    @Override
    public Mono<String> getTokenInRedisByUserId(String userid){
        return reactiveValueOperations.get(userid);
    }
}
