package com.verifymycoin.Apigateway.filter;

import com.verifymycoin.Apigateway.token.JwtTokenConfig;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtValidateFilter extends AbstractGatewayFilterFactory<JwtValidateFilter.Config> {

    private final JwtTokenConfig jwtTokenConfig;

    public JwtValidateFilter(JwtTokenConfig jwtTokenConfig) {
        super(JwtValidateFilter.Config.class);
        this.jwtTokenConfig = jwtTokenConfig;
    }

    @Override
    public GatewayFilter apply(JwtValidateFilter.Config config) {
        return ((ServerWebExchange exchange, GatewayFilterChain chain) -> {
            ServerWebExchange modifiedExchange;

            try{
                String jwt = jwtTokenConfig.resolveToken(exchange.getRequest());
                Claims claims = jwtTokenConfig.parseToken(jwt);
                String userId = claims.get("userId").toString();

                // TODO : Reactive한 로직?
                String jwtInRedis = jwtTokenConfig.getTokenInRedisByUserId(userId).share().block();
                System.out.println("jwtInRedis = " + jwtInRedis);
                if(jwtInRedis.isEmpty() || !jwtInRedis.equals(jwt)) throw new JwtException("does not exist token in database");

                modifiedExchange =  exchange
                        .mutate()
                        .request(request -> request.header("userId",userId))
                        .build();
            } catch (IllegalArgumentException | JwtException e){
                log.error("jwt exception : {}", e.toString());
                return setExchangeForUnauthorizedResponse(exchange);
            }
            return chain.filter(modifiedExchange);
        });
    }

    private Mono<Void> setExchangeForUnauthorizedResponse(ServerWebExchange exchange) {
        byte[] bytes = "{ \"code\" : \"401\", \"message\" : \"UNAUTHORIZED TOKEN\"} ".getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        exchange.getResponse().getHeaders().add("Content-Type", "\"application/json\"");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }


    @Data
    @AllArgsConstructor
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}


