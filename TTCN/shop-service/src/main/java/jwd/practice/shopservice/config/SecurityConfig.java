package jwd.practice.shopservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SecurityConfig {
    protected static final String KEY_SIGN = "lQgnbki8rjdh62RZ2FNXZB9KWYB1IjajiY04z011BXjjagnc7a";

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request ->
                request
//                        .requestMatchers("/admin/**").authenticated()
//                        .requestMatchers("/voucher/public/**").permitAll()
//                        .requestMatchers("/public/product/**").permitAll()
//                        .requestMatchers("/public/category/**").permitAll()
//                        .requestMatchers(("/review/public/**")).permitAll()
                        .requestMatchers("/shop/**").permitAll()
                        .requestMatchers("/authentication/**").permitAll()
                        .requestMatchers("/email/**").permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;
        http.oauth2ResourceServer(request
                -> request.jwt(jwtConfigurer
                -> jwtConfigurer.decoder(jwtDecoder())
                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())

        );
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }


    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY_SIGN.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
    @Bean
        // chuyen SCOPE -> ROLE
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter grantedConverter = new JwtGrantedAuthoritiesConverter();
        grantedConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter authenConverter = new JwtAuthenticationConverter();
        authenConverter.setJwtGrantedAuthoritiesConverter(grantedConverter);
        return authenConverter;
    }
}



