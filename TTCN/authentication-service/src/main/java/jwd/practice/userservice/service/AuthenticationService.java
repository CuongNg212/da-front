package jwd.practice.userservice.service;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jwd.practice.userservice.dto.request.AuthenticationRequest;
import jwd.practice.userservice.dto.request.IntrospectRequest;
import jwd.practice.userservice.dto.request.LogoutRequest;
import jwd.practice.userservice.dto.request.RefreshRequest;
import jwd.practice.userservice.dto.response.AuthenticationResponse;
import jwd.practice.userservice.dto.response.IntrospectResponse;
import jwd.practice.userservice.entity.InvalidToken;
import jwd.practice.userservice.entity.User;
import jwd.practice.userservice.exception.AppException;
import jwd.practice.userservice.exception.ErrException;
import jwd.practice.userservice.repository.InvalidTokenRepository;
import jwd.practice.userservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidTokenRepository tokenRepository;

    protected static final String KEY_SIGN = "lQgnbki8rjdh62RZ2FNXZB9KWYB1IjajiY04z011BXjjagnc7a";

    protected static final long VALID_DURATION = 86400;

    protected static final long REFRESHABLE_DURATION = 15;

    private String createToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(user.getUserId()))
                .issuer("Dater")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope",buildScopeToRole(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(KEY_SIGN.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();

        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (ParseException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }


    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(KEY_SIGN.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION,ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if(!verified && expiryTime.after(new Date())) {
            throw new AppException(ErrException.UNAUTHENTICATED);
        }

        if (tokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrException.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(logoutRequest.getToken(), false);

            String jwtID = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidToken invalidToken = InvalidToken.builder()
                    .id(jwtID)
                    .expiryTime(expiryTime)
                    .build();
            tokenRepository.save(invalidToken);
        } catch (ParseException e) {
            log.info("Token already expired");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException {
        var signToken = verifyToken(refreshRequest.getToken(), true);
        String jwtID = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidToken invalidToken = InvalidToken.builder()
                .id(jwtID)
                .expiryTime(expiryTime)
                .build();
        tokenRepository.save(invalidToken);

        var username = signToken.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrException.USER_NOT_EXISTED));

        var token = createToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .check(true)
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();
    }

    public AuthenticationResponse authenticationResponse(AuthenticationRequest authenticationRequest){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        var user = userRepository.findByUsernameOrEmail(
                authenticationRequest.getIdentifier(),
                authenticationRequest.getIdentifier()
        ).orElseThrow(() -> new AppException(ErrException.USER_NOT_EXISTED));
        boolean checked = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if (!checked) {
            throw new AppException(ErrException.UNAUTHENTICATED);
        }
        var token = createToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .check(true)
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();
    }

    public String buildScopeToRole(User user){
        StringJoiner scopeJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(scopeJoiner::add);
        }
        return scopeJoiner.toString();
    }
}
