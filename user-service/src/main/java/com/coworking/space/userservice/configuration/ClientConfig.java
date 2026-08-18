package com.coworking.space.userservice.configuration;

import com.coworking.space.userservice.clients.AuthClient;
import com.coworking.space.userservice.properties.AuthClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {
    @Bean
    public AuthClient authClient(AuthClientProperties properties) {
        RestClient restClient = RestClient.builder().baseUrl(properties.getBaseUrl()).build();

        var factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(AuthClient.class);
    }
}
