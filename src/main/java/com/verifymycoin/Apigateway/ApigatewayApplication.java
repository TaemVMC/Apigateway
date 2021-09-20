package com.verifymycoin.Apigateway;

import com.verifymycoin.Apigateway.token.JwtTokenConfig;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import reactor.core.publisher.Mono;

import java.io.Console;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class ApigatewayApplication implements ApplicationRunner {

	@Autowired
	private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	@Autowired
	private JwtTokenConfig jwtTokenConfig;

	public static void main(String[] args) {
		SpringApplication.run(ApigatewayApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {


//		setOps.add("key",);
//		setOps.add("key","test ests e");
//		setOps.members("key").buffer().subscribe(System.out::println);
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ2bWMiLCJpYXQiOjE2MzIxMzY3NTMsImV4cCI6MTYzMjEzODU1MywiaWQiOiLslYTsnbTrlJQiLCJlbWFpbCI6ImFqdWZyZXNoQGdtYWlsLmNvbSJ9.cusnAOSbgnq8wV6ir1zSN_mL5D8eBh2kmKLMFe8Yhqc";
		jwtTokenConfig.saveTokenInRedis("userid", token).subscribe();
		System.out.println("jwtTokenConfig.existToken(\"no member\", \"no token\") = " + jwtTokenConfig.existToken("no member", "no token").block());


//		TODO
//		key 없을 때 / value없을 때
		System.out.println(" get token by user id = " + jwtTokenConfig.getTokenByUserId("userid"));
//

	}

//	@Bean
//	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route("path_route", r -> r.path("/get")
//						.uri("http://httpbin.org"))
//				.route("host_route", r -> r.host("*.myhost.org")
//						.uri("http://httpbin.org"))
//				.route("rewrite_route", r -> r.host("*.rewrite.org")
//						.filters(f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}"))
//						.uri("http://httpbin.org"))
//				.route("hystrix_route", r -> r.host("*.hystrix.org")
//						.filters(f -> f.hystrix(c -> c.setName("slowcmd")))
//						.uri("http://httpbin.org"))
//				.route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
//						.filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
//						.uri("http://httpbin.org"))
//				.route("limit_route", r -> r
//						.host("*.limited.org").and().path("/anything/**")
//						.filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
//						.uri("http://httpbin.org"))
//				.build();
//	}
}


