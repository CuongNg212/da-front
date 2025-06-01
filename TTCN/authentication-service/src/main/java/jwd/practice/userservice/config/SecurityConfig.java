package jwd.practice.userservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JwtDecoderConfig jwtDecoderConfig;

    public static final String[] Public_Endpoints = {
        "/user/**",
            "/auth/**",
            "api/notification/.*" ,
            "/user/forgot-password"
    };
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request ->
                request.requestMatchers(Public_Endpoints).permitAll()
                        //hasAuthority("REJECT_POST")
                        //hasAnyAuthority("ROLE_ADMIN")
                        //hasRole(Role.ADMIN.name())
                        //.hasRole("ADMIN")
                        .anyRequest().permitAll()
        )
                .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);
        http.oauth2ResourceServer(request -> request.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoderConfig)));
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedConverter = new JwtGrantedAuthoritiesConverter();
        grantedConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter authenConverter = new JwtAuthenticationConverter();
        authenConverter.setJwtGrantedAuthoritiesConverter(grantedConverter);
        return authenConverter;
    }
}
