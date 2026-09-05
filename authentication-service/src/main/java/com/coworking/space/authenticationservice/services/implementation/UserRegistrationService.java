package com.coworking.space.authenticationservice.services.implementation;

import com.coworking.space.authenticationservice.dto.request.SingUpRequest;
import com.coworking.space.authenticationservice.dto.response.RegistrationStatusResponse;
import com.coworking.space.authenticationservice.infrustructure.properties.KafkaTopicsProperties;
import com.coworking.space.authenticationservice.mapers.UserInfoMapper;
import com.coworking.space.authenticationservice.services.AuthService;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {
    private final AuthService authService;
    private final KafkaAvroSerializer kafkaAvroSerializer;
    private final KafkaTopicsProperties properties;
    private final UserInfoMapper userInfoMapper;

    public RegistrationStatusResponse register(SingUpRequest request) {
        var userRegistrationRequest = userInfoMapper.toUserRegistrationRequest(request.getUserInfo());
        var payload = kafkaAvroSerializer.serialize(properties.getUserRegistrationTopic(), userRegistrationRequest);

        return authService.register(request, payload);
    }
}
