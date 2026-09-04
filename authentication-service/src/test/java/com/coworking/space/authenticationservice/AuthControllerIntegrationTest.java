package com.coworking.space.authenticationservice;

import com.coworking.space.authenticationservice.clients.UserClient;
import com.coworking.space.authenticationservice.dto.request.LoginRequest;
import com.coworking.space.authenticationservice.dto.request.RefreshRequest;
import com.coworking.space.authenticationservice.dto.request.SingUpRequest;
import com.coworking.space.authenticationservice.dto.request.UserCreateRequest;
import com.coworking.space.authenticationservice.dto.response.RegistrationStatusResponse;
import com.coworking.space.authenticationservice.dto.response.UserResponse;
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
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
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

    @AfterEach
    void clean() {
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

    @Test
    void shouldRegisterAndReturnValidJwtTokens() throws Exception {
        SingUpRequest request = new SingUpRequest("alex_dev", "password123", createValidUserInfo());

        String responseJson = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn().getResponse().getContentAsString();

        RegistrationStatusResponse authResponse = objectMapper.readValue(responseJson, RegistrationStatusResponse.class);

        Jwt accessJwt = jwtDecoder.decode(authResponse.getAccessToken());
        assertEquals("alex_dev", accessJwt.getSubject());
        assertEquals("access", accessJwt.getClaim("type"));
        assertTrue(accessJwt.getClaimAsStringList("roles").contains("ROLE_USER"));
        assertNotNull(accessJwt.getClaim("userId"));

        Jwt refreshJwt = jwtDecoder.decode(authResponse.getRefreshToken());
        assertEquals("alex_dev", refreshJwt.getSubject());
        assertEquals("refresh", refreshJwt.getClaim("type"));
    }

    @Test
    void shouldFailRegistrationWhenUsernameExists() throws Exception {
        SingUpRequest request = new SingUpRequest("duplicate_user", "password123", createValidUserInfo());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldAuthenticateAndReturnTokens() throws Exception {
        SingUpRequest signUp = new SingUpRequest("john_doe", "secret_pass", createValidUserInfo());
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUp)));

        LoginRequest login = new LoginRequest("john_doe", "secret_pass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        SingUpRequest signUp = new SingUpRequest("refresh_user", "secret_pass", createValidUserInfo());
        String signupResponse = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp)))
                .andReturn().getResponse().getContentAsString();

        RegistrationStatusResponse initialTokens = objectMapper.readValue(signupResponse, RegistrationStatusResponse.class);

        RefreshRequest refreshRequest = new RefreshRequest(initialTokens.getRefreshToken());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void shouldRejectRefreshWhenAccessTokenProvidedInsteadOfRefreshToken() throws Exception {
        SingUpRequest signUp = new SingUpRequest("hacker_user", "secret_pass", createValidUserInfo());
        String signupResponse = mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp)))
                .andReturn().getResponse().getContentAsString();

        RegistrationStatusResponse tokens = objectMapper.readValue(signupResponse, RegistrationStatusResponse.class);

        RefreshRequest invalidRefreshRequest = new RefreshRequest(tokens.getAccessToken());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRefreshRequest)))
                .andExpect(status().isUnauthorized());
    }
}