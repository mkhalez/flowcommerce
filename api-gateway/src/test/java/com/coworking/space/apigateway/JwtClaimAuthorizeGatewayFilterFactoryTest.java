package com.coworking.space.apigateway;

import com.coworking.space.apigateway.filters.JwtClaimAuthorizeGatewayFilterFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class JwtClaimAuthorizeGatewayFilterFactoryTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtClaimAuthorizeGatewayFilterFactory factory = new JwtClaimAuthorizeGatewayFilterFactory(objectMapper);

    @Test
    void shouldPassWhenClaimMatches() {
        var config = new JwtClaimAuthorizeGatewayFilterFactory.Config();
        config.setClaimName("type");
        config.setExpectedValue("access");

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("type", "access")
                .build();
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/order/1"));
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(
                factory.apply(config).filter(exchange, chain)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
        ).verifyComplete();

        verify(chain).filter(exchange);
    }

    @Test
    void shouldReturn403WhenClaimDoesNotMatch() {
        JwtClaimAuthorizeGatewayFilterFactory.Config config = new JwtClaimAuthorizeGatewayFilterFactory.Config();
        config.setClaimName("type");
        config.setExpectedValue("access");

        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none")
                .claim("type", "refresh").build();
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);

        MockServerWebExchange exchange =
                MockServerWebExchange.from(MockServerHttpRequest.get("/api/order/1"));
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        StepVerifier.create(
                factory.apply(config).filter(exchange, chain)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
        ).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(any());
    }

}
