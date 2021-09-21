package com.verifymycoin.Apigateway.filter;

import com.verifymycoin.Apigateway.token.JwtTokenConfig;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
@Component
@Slf4j
public class JwtRequestFilter extends AbstractGatewayFilterFactory<JwtRequestFilter.Config> {

    private final JwtTokenConfig jwtTokenConfig;

    public JwtRequestFilter(JwtTokenConfig jwtTokenConfig) {
        super(JwtRequestFilter.Config.class);
        this.jwtTokenConfig = jwtTokenConfig;
    }

    @Override
    public GatewayFilter apply(JwtRequestFilter.Config config) {
        return ((exchange, chain) -> {
            log.info("Before proxy -> JwtRequestFilter start");
            ServerWebExchange modifiedExchange ;
            try {

//                validation token
                String token = exchange.getRequest().getHeaders().getFirst("Authorization");
                String userId = jwtTokenConfig.parseJwtToken(token).get("id").toString();
                jwtTokenConfig.existToken(userId).subscribe(existToken -> {
                    if(existToken.isEmpty() || !existToken.equals(token)) throw new JwtException("does not exist token in database");
                });

//                userId setting in requestHeader
                modifiedExchange =  exchange
                                    .mutate()
                                    .request(request -> request.header("userId", userId))
                                    .build();

            } catch (JwtException | IllegalArgumentException| NullPointerException jwtException) {
                log.error("jwtException = {}" , jwtException.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(modifiedExchange);
        });
    }


//            then(Mono.fromRunnable(()->{
//                log.info("After proxy -> JwtRequestFilter start");
////                ServerHttpResponse response = exchange.getResponse();
////
////                if (response.getStatusCode() == HttpStatus.OK){
////                    String jwt = jwtTokenConfig.makeToken();
////
////
////                    // TODO : jwt 저장
////
////                    // TODO : Response 에 JWT 포함
////                    System.out.println(response.toString());
////                    response.getHeaders().add("jwt", jwt);
////
////                }else{
////                    //TODO : 그대로 반환
////
////                }
//
//
//            }));
//

//            if (config.isPreLogger()) {
//                log.info("JwtRequestFilter Start>>>>>>" + exchange.getRequest());
//            }
//
//            return chain.filter(exchange).then(Mono.fromRunnable(()->{
//                if (config.isPostLogger()) {
//                    log.info("VerificationFilter End>>>>>>" + exchange.getResponse());
//                }
//            })); }


    @Data
    @AllArgsConstructor
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}


