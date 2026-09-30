package com.coworking.space.orderservice.configuration;

import com.coworking.space.orderservice.clients.UserServiceClient;
import com.coworking.space.orderservice.properties.ClientProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ClientConfig {
    @Bean
    public UserServiceClient userServiceClient(ClientHttpRequestInterceptor jwtInterceptor,
                                               ClientProperties clientProperties) {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(clientProperties.getConnectionTimeout()))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(clientProperties.getReadTimeout()));

        RestClient restClient = RestClient.builder()
                .baseUrl(clientProperties.getBaseUrl())
                .requestFactory(requestFactory)
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
            if(auth instanceof JwtAuthenticationToken jwtAuthentication) {
                request.getHeaders().setBearerAuth(jwtAuthentication.getToken().getTokenValue());
            }

            return execution.execute(request, body);
        });
    }
}
