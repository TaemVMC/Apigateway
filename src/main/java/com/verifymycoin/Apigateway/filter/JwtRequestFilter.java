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
import reactor.core.publisher.Mono;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


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
            //TODO :  Redis 토큰 있는지, 유효하냐.
            try {
                String token = exchange.getRequest().getHeaders().getFirst("Authorization");
                System.out.println("JWT token (extracted) = " + token);
                String userId = jwtTokenConfig.parseJwtToken(token).getId();
                if(FALSE.equals(jwtTokenConfig.existToken(userId, token).block())) {
                    throw new JwtException("does not exist token in database");
                }
            } catch (JwtException jwtException) {
                //TODO reject
                log.error("jwtException = {}" , jwtException.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
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


