package jwd.practice.userservice.controller;


import com.nimbusds.jose.JOSEException;
import jwd.practice.userservice.dto.request.AuthenticationRequest;
import jwd.practice.userservice.dto.request.IntrospectRequest;
import jwd.practice.userservice.dto.request.LogoutRequest;
import jwd.practice.userservice.dto.request.RefreshRequest;
import jwd.practice.userservice.dto.response.ApiResponse;
import jwd.practice.userservice.dto.response.AuthenticationResponse;
import jwd.practice.userservice.dto.response.IntrospectResponse;
import jwd.practice.userservice.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> createToken(@RequestBody AuthenticationRequest authenticationRequest) {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("login success")
                .result(authenticationService.authenticationResponse(authenticationRequest))
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspectToken(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        return ApiResponse.<IntrospectResponse>builder()
                .code(200)
                .message("login success")
                .result(authenticationService.introspect(introspectRequest))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("logout success")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> createToken(@RequestBody RefreshRequest refreshRequest) throws ParseException, JOSEException {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("login success")
                .result(authenticationService.refreshToken(refreshRequest))
                .build();
    }
}
