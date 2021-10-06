package com.verifymycoin.Apigateway.token;

import io.jsonwebtoken.Claims;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;


public interface JwtTokenConfig {
    public String makeToken(String userId, String email);
    public Claims parseToken(String J);
    public String resolveToken(ServerHttpRequest request);
//TODO Boolean value check
    public Mono<Boolean> saveTokenInRedis(String userId, String jwt);

    public Mono<String> getTokenInRedisByUserId(String userid);
}
