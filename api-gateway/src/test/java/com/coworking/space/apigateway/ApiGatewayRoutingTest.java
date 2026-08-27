package com.coworking.space.apigateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ApiGatewayRoutingTest {
    static WireMockServer authService = new WireMockServer(0);
    static WireMockServer orderService = new WireMockServer(0);
    static WireMockServer userService = new WireMockServer(0);

    @Autowired
    WebTestClient webTestClient;

    @BeforeAll
    static void startWireMock() {
        authService.start();
        orderService.start();
        userService.start();
    }

    @AfterAll
    static void stopWireMock() {
        authService.stop();
        orderService.stop();
        userService.stop();
    }

    @BeforeEach
    void resetWireMock() {
        authService.resetAll();
        orderService.resetAll();
        userService.resetAll();
    }

    @DynamicPropertySource
    static void overrideRoutes(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.gateway.server.webflux.routes[0].id", () -> "auth-refresh-route");
        registry.add("spring.cloud.gateway.server.webflux.routes[0].uri", () -> "http://localhost:" + authService.port());
        registry.add("spring.cloud.gateway.server.webflux.routes[0].predicates[0]", () -> "Path=/api/auth/refresh");
        registry.add("spring.cloud.gateway.server.webflux.routes[0].filters[0]", () -> "JwtClaimAuthorize=type,refresh");

        registry.add("spring.cloud.gateway.server.webflux.routes[1].id", () -> "auth-route");
        registry.add("spring.cloud.gateway.server.webflux.routes[1].uri", () -> "http://localhost:" + authService.port());
        registry.add("spring.cloud.gateway.server.webflux.routes[1].predicates[0]", () -> "Path=/api/auth/**");

        registry.add("spring.cloud.gateway.server.webflux.routes[2].id", () -> "order-route");
        registry.add("spring.cloud.gateway.server.webflux.routes[2].uri", () -> "http://localhost:" + orderService.port());
        registry.add("spring.cloud.gateway.server.webflux.routes[2].predicates[0]", () -> "Path=/api/order/**");
        registry.add("spring.cloud.gateway.server.webflux.routes[2].filters[0]", () -> "JwtClaimAuthorize=type,access");

        registry.add("spring.cloud.gateway.server.webflux.routes[3].id", () -> "user-route");
        registry.add("spring.cloud.gateway.server.webflux.routes[3].uri", () -> "http://localhost:" + userService.port());
        registry.add("spring.cloud.gateway.server.webflux.routes[3].predicates[0]", () -> "Path=/api/user/**");
        registry.add("spring.cloud.gateway.server.webflux.routes[3].filters[0]", () -> "JwtClaimAuthorize=type,access");
    }

    @TestConfiguration
    static class JwtDecoderTestConfig {
        @Bean
        ReactiveJwtDecoder reactiveJwtDecoder() {
            return NimbusReactiveJwtDecoder
                    .withPublicKey((RSAPublicKey) TestKeys.KEY_PAIR.getPublic())
                    .build();
        }
    }

    @Test
    void shouldForwardRequestToOrderServiceWithAccessToken() {
        orderService.stubFor(get(urlEqualTo("/api/order/1"))
                .willReturn(aResponse().withStatus(200).withBody("{\"id\":1}")));

        String token = TestJwt.build(Map.of("type", "access", "roles", List.of("USER")));

        webTestClient.get().uri("/api/order/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk();

        orderService.verify(getRequestedFor(urlEqualTo("/api/order/1"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer " + token)));
    }

    @Test
    void shouldReturn403WhenTokenTypeIsRefreshOnOrderRoute() {
        String token = TestJwt.build(Map.of("type", "refresh"));

        webTestClient.get().uri("/api/order/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isForbidden();

        orderService.verify(0, getRequestedFor(urlEqualTo("/api/order/1")));
    }

    @Test
    void shouldReturn401WhenNoTokenOnAuthenticatedRoute() {
        webTestClient.get().uri("/api/order/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldPermitAllOnLoginRouteWithoutToken() {
        authService.stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse().withStatus(200)));

        webTestClient.post().uri("/api/auth/login")
                .exchange()
                .expectStatus().isOk();
    }

    static class TestJwt {
        static String build(Map<String, Object> claims) {
            try {
                RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) TestKeys.KEY_PAIR.getPublic())
                        .privateKey(TestKeys.KEY_PAIR.getPrivate())
                        .keyID("test-key")
                        .build();
                JWSSigner signer = new RSASSASigner(rsaKey);

                JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                        .subject("test-user")
                        .issueTime(new Date())
                        .expirationTime(new Date(System.currentTimeMillis() + 60_000));
                claims.forEach(builder::claim);

                SignedJWT jwt = new SignedJWT(
                        new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("test-key").build(),
                        builder.build());
                jwt.sign(signer);
                return jwt.serialize();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static public final class TestKeys {

        public static final KeyPair KEY_PAIR = generate();

        private TestKeys() {
        }

        private static KeyPair generate() {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                return generator.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("Failed to generate RSA key pair for tests", e);
            }
        }
    }
}
