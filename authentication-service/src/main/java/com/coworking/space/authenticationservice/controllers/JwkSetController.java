package com.coworking.space.authenticationservice.controllers;

import com.coworking.space.authenticationservice.properties.RsaKeyProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwkSetController {
    private final RsaKeyProperties rsaKeys;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> hello() {
        RSAKey jwk = new RSAKey.Builder(rsaKeys.getPublicKey())
                .keyID(rsaKeys.getKid())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();

        return new JWKSet(jwk).toJSONObject();
    }
}
