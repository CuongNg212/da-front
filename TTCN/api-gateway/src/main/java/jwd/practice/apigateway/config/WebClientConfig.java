package jwd.practice.apigateway.config;

import jwd.practice.apigateway.repository.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebClientConfig {
    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/authentication")
                .build();
    }

    @Bean
    IdentityClient identityClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();
        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }

//    @Bean
//    CorsWebFilter corsWebFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        config.setAllowedOrigins(List.of(
//                "http://localhost:3000", "http://localhost:5501",
//                "http://localhost:4173", "http://localhost:5173",
//                "http://127.0.0.1:5500", "http://127.0.0.1:5501",
//                "http://127.0.0.1:5502"
//        ));
//
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "x-no-retry"));
//        config.setAllowCredentials(true);
//        config.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsWebFilter(source);
//    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500")); // Chỉ định cụ thể
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "x-no-retry"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }



}
