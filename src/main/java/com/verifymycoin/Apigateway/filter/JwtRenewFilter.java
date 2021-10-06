package com.verifymycoin.Apigateway.filter;

import com.verifymycoin.Apigateway.token.JwtTokenConfig;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtRenewFilter extends AbstractGatewayFilterFactory<JwtRenewFilter.Config> {

    private final JwtTokenConfig jwtTokenConfig;

    public JwtRenewFilter(JwtTokenConfig jwtTokenConfig) {
        super(JwtRenewFilter.Config.class);
        this.jwtTokenConfig = jwtTokenConfig;
    }

    @Override
    public GatewayFilter apply(JwtRenewFilter.Config config) {
        return ((ServerWebExchange exchange, GatewayFilterChain chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                String jwt = jwtTokenConfig.resolveToken(exchange.getRequest());
                Claims claims = jwtTokenConfig.parseToken(jwt);
                String userId = claims.get("userId").toString();
                String userEmail = claims.get("email").toString();


                String newJwt = jwtTokenConfig.makeToken(userId, userEmail);
                jwtTokenConfig.saveTokenInRedis(userId, newJwt).share().block();
                exchange.getResponse().getHeaders().add("jwt",newJwt);
            }));
        });
    }

    @Data
    @AllArgsConstructor
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}


