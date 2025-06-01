package jwd.practice.apigateway.service;


import jwd.practice.apigateway.dto.ApiResponse;
import jwd.practice.apigateway.dto.request.IntrospectRequest;
import jwd.practice.apigateway.dto.response.IntrospectResponse;
import jwd.practice.apigateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;
    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return identityClient.introspect(IntrospectRequest.builder()
                        .token(token)
                .build());
    }
}
