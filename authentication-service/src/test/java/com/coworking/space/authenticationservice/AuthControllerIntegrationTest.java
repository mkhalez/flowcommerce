package com.coworking.space.authenticationservice;

import com.coworking.space.authenticationservice.clients.UserClient;
import com.coworking.space.authenticationservice.domain.statuses.RegistrationEventStatus;
import com.coworking.space.authenticationservice.dto.request.LoginRequest;
import com.coworking.space.authenticationservice.dto.request.RefreshRequest;
import com.coworking.space.authenticationservice.dto.request.SingUpRequest;
import com.coworking.space.authenticationservice.dto.request.UserCreateRequest;
import com.coworking.space.authenticationservice.dto.response.AuthResponse;
import com.coworking.space.authenticationservice.dto.response.RegistrationStatusResponse;
import com.coworking.space.authenticationservice.dto.response.UserResponse;
import com.coworking.space.authenticationservice.repositories.RegistrationEventRepository;
import com.coworking.space.authenticationservice.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDate;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private UserClient userClient;

    @Autowired
    private RegistrationEventRepository registrationEventRepository;

    @AfterEach
    void clean() {
        registrationEventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUpMocks() {
        UserResponse mockedResponse = new UserResponse(
                1,
                "John",
                "Doe",
                LocalDate.of(1995, 5, 20),
                "john.doe@example.com",
                0,
                true
        );

        Mockito.when(userClient.createUser(any())).thenReturn(mockedResponse);
    }

    private UserCreateRequest createValidUserInfo() {
        return new UserCreateRequest(
                "John",
                "Doe",
                LocalDate.of(1995, 5, 20),
                "john.doe@example.com",
                null
        );
    }

    private RegistrationStatusResponse signUp(SingUpRequest request) throws Exception {
        String responseJson = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.status").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(responseJson, RegistrationStatusResponse.class);
    }

    private void awaitRegistrationCompleted(java.util.UUID transactionId) {
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            String json = mockMvc.perform(get("/api/auth/status/{registrationId}", transactionId))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            RegistrationStatusResponse statusResponse =
                    objectMapper.readValue(json, RegistrationStatusResponse.class);

            assertEquals(RegistrationEventStatus.CREATED, statusResponse.getStatus());
        });
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        SingUpRequest request = new SingUpRequest("alex_dev", "password123", createValidUserInfo());

        RegistrationStatusResponse registrationResponse = signUp(request);

        assertNotNull(registrationResponse.getTransactionId());
        awaitRegistrationCompleted(registrationResponse.getTransactionId());
    }

    @Test
    void shouldFailRegistrationWhenUsernameExists() throws Exception {
        SingUpRequest request = new SingUpRequest("duplicate_user", "password123", createValidUserInfo());

        RegistrationStatusResponse first = signUp(request);
        awaitRegistrationCompleted(first.getTransactionId());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}