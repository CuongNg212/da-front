package jwd.practice.apigateway.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jwd.practice.apigateway.dto.ApiResponse;
import jwd.practice.apigateway.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService identityService;
    ObjectMapper objectMapper;

    @NonFinal
    private String[] publicEndpoints = {
            "/api/authentication/auth/.*",
            "/api/authentication/user/.*",
            "/api/notification/email/send",
            "/api/shop/.*",
//            "/api/shop/public/category/.*",
//            "/api/shop/voucher/public/.*",
//            "/api/shop/review/public/.*"
    };

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("AuthenticationFilter...");

        if (isPublicEndpoint(exchange.getRequest())){
            return chain.filter(exchange);
        }

        // get token from authorization header
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader)) {
            return unauthenticated(exchange.getResponse());
        }

        String token = authHeader.get(0).replace("Bearer ", "");
        log.info("Authentication token: {}" , token);

        return identityService.introspect(token).flatMap(introspectResponse -> {
            if (introspectResponse.getResult().isValid()){
                return chain.filter(exchange);
            } else {
                return unauthenticated(exchange.getResponse());
            }
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        log.info("Checking public endpoint for path: {}", path);
        boolean isMatch = Arrays.stream(publicEndpoints).anyMatch(s -> path.matches(s));
        log.info("Is public endpoint: {}", isMatch);
        return isMatch;
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(404)
                .message("Unauthorized")
                .build();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

}
