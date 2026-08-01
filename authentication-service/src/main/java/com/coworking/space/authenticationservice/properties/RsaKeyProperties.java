package com.coworking.space.authenticationservice.properties;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "security.keys")
@AllArgsConstructor
@Getter
@Setter
public class RsaKeyProperties {
    private RSAPublicKey publicKey;

    private RSAPrivateKey privateKey;

    @NotNull
    private String kid;
}
