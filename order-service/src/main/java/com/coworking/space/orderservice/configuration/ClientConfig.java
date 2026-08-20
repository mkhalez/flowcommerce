package com.coworking.space.orderservice.configuration;

import com.coworking.space.orderservice.clients.UserServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {
    @Value("${user-service.base-url}")
    private String baseUrl;

    @Bean
    public UserServiceClient userServiceClient(ClientHttpRequestInterceptor jwtInterceptor) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(jwtInterceptor)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(adapter).build();

        return httpServiceProxyFactory.createClient(UserServiceClient.class);
    }

    @Bean
    public ClientHttpRequestInterceptor jwtInterceptor() {
        return ((request, body, execution) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                request.getHeaders().setBearerAuth(jwt.getTokenValue());
            }

            return execution.execute(request, body);
        });
    }
}
