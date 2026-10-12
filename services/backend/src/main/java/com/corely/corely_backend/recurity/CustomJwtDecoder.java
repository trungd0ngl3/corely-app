package com.corely.corely_backend.recurity;

import com.corely.corely_backend.repository.InvalidatedTokenRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CustomJwtDecoder implements JwtDecoder {

    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signer-key}")
    String SIGNER_KEY;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            String algorithm = signedJWT.getHeader()
                    .getAlgorithm()
                    .getName();

            if (!"HS512".equals(algorithm)) {
                throw new BadJwtException("Unsupported algorithm");
            }

            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes(StandardCharsets.UTF_8));
            boolean verified = signedJWT.verify(verifier);

            if (!verified) {
                throw new BadJwtException("Token signature verification failed");
            }

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            
            // Check type
            String type = claimsSet.getStringClaim("type");
            if (!"access".equals(type)) {
                throw new BadJwtException("Invalid token type");
            }

            // Check issuer
            String issuer = claimsSet.getIssuer();

            if (!"corely-backend".equals(issuer)) {
                throw new BadJwtException("Invalid issuer");
            }

            // Check expiration
            Date expirationTime = claimsSet.getExpirationTime();
            if (expirationTime != null && expirationTime.before(new Date())) {
                throw new BadJwtException("Token has expired");
            }

            // Check if token has been invalidated (logout)
            String jwtId = claimsSet.getJWTID();
            if (jwtId != null && invalidatedTokenRepository.existsById(jwtId)) {
                throw new BadJwtException("Token has been invalidated");
            }

            // Build Spring Security Jwt object
            Instant issuedAt = claimsSet.getIssueTime() != null
                    ? claimsSet.getIssueTime().toInstant()
                    : Instant.now();
            Instant expiresAt = expirationTime != null
                    ? expirationTime.toInstant()
                    : Instant.now().plusSeconds(3600);

            Map<String, Object> headers = Map.of(
                    "alg", signedJWT.getHeader().getAlgorithm().getName(),
                    "typ", "JWT");

            return new Jwt(token, issuedAt, expiresAt, headers, claimsSet.getClaims());

        } catch (ParseException | JOSEException e) {
            throw new BadJwtException(e.getMessage(), e);
        }
    }
}
