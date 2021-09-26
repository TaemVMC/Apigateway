package com.verifymycoin.Apigateway.config;

import com.verifymycoin.Apigateway.filter.JwtRenewFilter;
import com.verifymycoin.Apigateway.filter.JwtValidateFilter;
import com.verifymycoin.Apigateway.token.JwtTokenConfig;


import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
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
    @Value("${vmcServicesAddr.UserManager}")
    private String userManagerAddr;

    @Value("${vmcServicesAddr.VerificationManager}")
    private String verificationManagerAddr;

    @Value("${vmcServicesAddr.TransactionManager}")
    private String transactionManagerAddr;

    private final JwtTokenConfig jwtTokenConfig;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtRenewFilter jwtRenewFilter, JwtValidateFilter jwtValidateFilter) {
        return builder.routes()
                .route("signin_route", r -> r.path("/user/signin")
                        .filters(f -> f
                                .modifyResponseBody(String.class, String.class, this::createJWToken))
                        .uri(userManagerAddr))
                .route("signup_route", r -> r.path("/user/signup")
                        .filters(f -> f
                                .modifyResponseBody(String.class, String.class, this::createJWToken))
                        .uri("http://localhost:8003/"))
                .route(r -> r.path("/user/**")
                        .filters(f -> f
                                .filter(jwtValidateFilter.apply(new JwtValidateFilter.Config("dummy", true, false)))
                                .filter(jwtRenewFilter.apply(new JwtRenewFilter.Config("dummy", true, false))))
                        .uri(userManagerAddr))
                .route(r -> r.path("/verification/**")
                        .filters(f -> f
                                .filter(jwtValidateFilter.apply(new JwtValidateFilter.Config("dummy", true, false)))
                                .filter(jwtRenewFilter.apply(new JwtRenewFilter.Config("dummy", true, false))))
                        .uri(verificationManagerAddr))
                .route(r -> r.path("/exchange/**")
                        .filters(f -> f
                                .filter(jwtValidateFilter.apply(new JwtValidateFilter.Config("dummy", true, false)))
                                .filter(jwtRenewFilter.apply(new JwtRenewFilter.Config("dummy", true, false))))
                        .uri(transactionManagerAddr))
                .build();
    }

    /**
    * UserManager에서 회원가입/로그인이 완료되면 새로운 JWT를 생성하고 redis에 저장한다.
    * */
    private Publisher<String> createJWToken(ServerWebExchange exchange, String body) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject rsBodyFromUserMgr = new JSONObject(body).getJSONObject("data");

            String jwt = jwtTokenConfig.makeToken(rsBodyFromUserMgr.getString("userId"), rsBodyFromUserMgr.getString("email"));
            response.getHeaders().add("jwt", jwt);
//            jsObject.put("jwt", jwt);
//            body = jsObject.toString();

            jwtTokenConfig
                    .saveTokenInRedis(rsBodyFromUserMgr.getString("userId"), jwt)
                    .subscribe();
        }
        return Mono.just(body);
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new CustomErrorAttributes();
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


