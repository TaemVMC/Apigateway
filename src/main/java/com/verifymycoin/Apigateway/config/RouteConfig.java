package com.verifymycoin.Apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;


import com.verifymycoin.Apigateway.filter.JwtRequestFilter;
import com.verifymycoin.Apigateway.token.JwtTokenConfig;


import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class RouteConfig {

    private final JwtTokenConfig jwtTokenConfig;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtRequestFilter jwtRequestFilter) {

        return builder.routes()
                .route("signin_route", r -> r.path("/user/signin")
                        .filters(f -> f
                                .modifyResponseBody(String.class, String.class, this::addJWToken))
//                                .filter(jwtRequestFilter.apply(new JwtRequestFilter.Config("dummy", true, false))))
                        .uri("http://localhost:8003/"))
                .route("signup_route", r -> r.path("/user/signup")
                        .filters(f -> f
                                .modifyResponseBody(String.class, String.class, this::addJWToken))
                        .uri("http://localhost:8003/"))
                .route(r -> r.path("/user/**")
                        .filters(f -> f
                                .filter(jwtRequestFilter.apply(new JwtRequestFilter.Config("dummy", true, false)))
                                .modifyResponseBody(String.class, String.class, this::addJWToken))
                        .uri("http://localhost:8003"))
                .build();
    }

    private Publisher<String> addJWToken(ServerWebExchange exchange, String body) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.getStatusCode() == HttpStatus.OK) {
//            TODO : list 처리 1. [ -> 2.  \\ jwt key
//            {
//              data : data ,
//            data : data
//            }
//            { jwt : ~~ ,
//                code : 1001,
//            message : ~~ ,
            //                       {
//                          data : data ,
//                          data : data
//                          }
//            }
            JSONObject jsObject = new JSONObject(body);
            String jwt = jwtTokenConfig.makeToken(jsObject.getString("userId"), jsObject.getString("email"));
            jsObject.put("jwt", jwt);
            body = jsObject.toString();

            jwtTokenConfig
                    .saveTokenInRedis(jsObject.getString("userId"), jwt)
                    .subscribe();
        }
        return Mono.just(body);
    }

//    @Bean
//    public RouteLocator verificationRouteLocator(RouteLocatorBuilder builder, VerificationFilter verificationFilter) {
//
//        return builder.routes()
//                .route(p -> p
//                        .path("/verification")
//                        .filters(verificationFilter.apply(new verificationFilter.Config("dummy", true, false)))
//                        .uri("http://localhost:8001"))
//                .build();
//    }

//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, ) {
//
//        return builder.routes()
//                .route("path_route",  r-> r.path("/user/**")
//                        .filters(f -> f
//                                .filter(jwtFilter.apply(new JwtRequestFilter.Config("dummy", true, false)))
//                                .addRequestHeader("Hello", ""))
//                                .uri("http://localhost:8003"))
//                        .build();
//    }
//
}


