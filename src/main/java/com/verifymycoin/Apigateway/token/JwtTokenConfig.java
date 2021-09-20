package com.verifymycoin.Apigateway.token;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;


public interface JwtTokenConfig {
    public String makeToken(String userId, String useremail);
    public Claims parseJwtToken(String authorizationHeader);
    public boolean checkExpirationToken(Claims claims);
//TODO Boolean value check
    public Mono<Boolean> saveTokenInRedis(String userId, String jwt);

    public Mono<String> getTokenByUserId(String userid);

    public Mono<String> existToken(String userid, String jwt);

}
