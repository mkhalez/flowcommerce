package com.coworking.space.apigateway.filters;

import lombok.*;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtClaimAuthorizeGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtClaimAuthorizeGatewayFilterFactory.Config> {
    private final ObjectMapper objectMapper;

    private static final String ERROR_MESSAGE = "token should be ";
    private static final String CLAIM_NAME = "claimName";
    private static final String EXPECTED_VALUE = "expectedValue";

    public JwtClaimAuthorizeGatewayFilterFactory(ObjectMapper objectMapper) {
        super(Config.class);
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of(CLAIM_NAME, EXPECTED_VALUE);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .map(auth -> ((JwtAuthenticationToken)auth).getToken())
                .flatMap(jwt -> {
                    String claimValue = jwt.getClaimAsString(config.getClaimName());
                    if(config.expectedValue.equals(claimValue)) {
                        return chain.filter(exchange);
                    }

                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    response.setStatusCode(HttpStatus.FORBIDDEN);

                    String message = ERROR_MESSAGE + config.expectedValue;
                    byte[] bytes = objectMapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = response.bufferFactory().wrap(bytes);
                    return response.writeWith(Mono.just(buffer));
                }));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Config {
        private String claimName;
        private String expectedValue;
    }
}
