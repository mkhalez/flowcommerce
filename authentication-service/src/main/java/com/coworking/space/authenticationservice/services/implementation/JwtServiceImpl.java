package com.coworking.space.authenticationservice.services.implementation;

import com.coworking.space.authenticationservice.domain.models.Role;
import com.coworking.space.authenticationservice.properties.AuthSecurityProperties;
import com.coworking.space.authenticationservice.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final AuthSecurityProperties authSecurityProperties;
    private final JwtEncoder encoder;

    private static final String ISSUER = "auth-service";
    private static final String ROLES_CLAIM_NAME = "roles";
    private static final String REFRESH_CLAIM_TYPE = "refresh";
    private static final String ACCESS_CLAIM_TYPE = "access";
    private static final String TOKEN_TYPE_NAME = "type";
    private static final String USER_ID_NAME = "userId";

    @Override
    public String generateAccessToken(String  username, Set<Role> roles, int userId) {
        Instant now = Instant.now();

        var claimRoles = roles.stream()
                .map(Role::getName)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(authSecurityProperties.getAccessTokenMinutes(), ChronoUnit.MINUTES))
                .subject(username)
                .claim(ROLES_CLAIM_NAME, claimRoles)
                .claim(TOKEN_TYPE_NAME, ACCESS_CLAIM_TYPE)
                .claim(USER_ID_NAME, userId)
                .build();

        var encoderParameters = JwtEncoderParameters.from(JwsHeader.with(SignatureAlgorithm.RS256).build(), claims);
        return this.encoder.encode(encoderParameters).getTokenValue();
    }

    @Override
    public String generateRefreshToken(String  username) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(authSecurityProperties.getRefreshTokenMinutes(), ChronoUnit.MINUTES))
                .subject(username)
                .claim(TOKEN_TYPE_NAME, REFRESH_CLAIM_TYPE)
                .build();

        var encoderParameters = JwtEncoderParameters.from(JwsHeader.with(SignatureAlgorithm.RS256).build(), claims);
        return this.encoder.encode(encoderParameters).getTokenValue();
    }
}
