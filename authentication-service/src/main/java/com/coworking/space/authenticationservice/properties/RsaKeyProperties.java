package com.coworking.space.authenticationservice.properties;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "security.keys")
@AllArgsConstructor
@Getter
public class RsaKeyProperties {
    private RSAPublicKey publicKey;

    private RSAPrivateKey privateKey;

    @NotNull
    private String kid;
}
