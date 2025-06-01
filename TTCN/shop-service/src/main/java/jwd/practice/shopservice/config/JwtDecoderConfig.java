package jwd.practice.shopservice.config;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class JwtDecoderConfig implements JwtDecoder {



    @Override
    public Jwt decode(String token) throws JwtException {
//        try {
//            var response = authenticationService.introspect(IntrospectRequest.builder()
//                            .token(token)
//                    .build());
//            if (!response.isValid()){
//                throw new JwtException("invalid token");
//            }
//        } catch (JOSEException | ParseException e) {
//            throw new JwtException(e.getMessage());
//        }  vi tang service nam ben duoi api gateway thi buoc nay se bi trung voi apigateway

//        if (Objects.isNull(nimbusJwtDecoder)) {
//            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY_SIGN.getBytes(), "HS256");
//            nimbusJwtDecoder = NimbusJwtDecoder
//                    .withSecretKey(secretKeySpec)
//                    .macAlgorithm(MacAlgorithm.HS256)
//                    .build();
//        }
//        return nimbusJwtDecoder.decode(token);
// vi tang service nam ben duoi api gateway thi buoc nay se bi trung voi apigateway
       try {
           SignedJWT signedJWT = SignedJWT.parse(token);
           return new Jwt(token,
                   signedJWT.getJWTClaimsSet().getIssueTime().toInstant(),
                   signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(),
                   signedJWT.getHeader().toJSONObject(),
                   signedJWT.getJWTClaimsSet().getClaims());
       } catch (ParseException e) {
           throw new JwtException("Invalid JWT");
       }
    }
}
