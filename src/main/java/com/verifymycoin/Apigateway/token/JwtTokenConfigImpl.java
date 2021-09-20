package com.verifymycoin.Apigateway.token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenConfigImpl implements JwtTokenConfig{

    @Value("${token.secret}")
    private String secret;

    private final ReactiveValueOperations<String, String> reactiveValueOperations;

    @Override
    public String makeToken(String userId, String useremail) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .setIssuer("vmc") // (2)
                .setIssuedAt(now) // (3)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis())) // (4)
                .claim("id", userId) // (5)
                .claim("email", useremail)
                .signWith(SignatureAlgorithm.HS256, secret) // (6)
                .compact();
    }

    @Override
    public Claims parseJwtToken(String authorizationHeader) {
        validationAuthorizationHeader(authorizationHeader); // (1)
        String token = extractToken(authorizationHeader); // (2)

        return Jwts.parser()
                .setSigningKey(secret) // (3)
                .parseClaimsJws(token) // (4)
                .getBody();
    }

    @Override
    public boolean checkExpirationToken(Claims claims) {
//        exist redis and timeout check

        return false;
    }

    public Mono<Boolean> saveTokenInRedis(String userId, String jwt) {
        return reactiveValueOperations.set(userId, jwt, Duration.ofMinutes(30));
    }

    @Override
    public Mono<String> getTokenByUserId(String userid) {
        return reactiveValueOperations.get(userid);
    }

    @Override
    public Mono<String> existToken(String userid, String jwt) {
        return reactiveValueOperations.get(userid);
    }

    private void validationAuthorizationHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }
    }

    private String extractToken(String authorizationHeader) {
        return authorizationHeader.substring("Bearer ".length());
    }
}
