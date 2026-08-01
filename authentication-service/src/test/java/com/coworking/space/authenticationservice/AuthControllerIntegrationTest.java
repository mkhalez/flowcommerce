package com.coworking.space.authenticationservice;

import com.coworking.space.authenticationservice.dto.request.LoginRequest;
import com.coworking.space.authenticationservice.dto.request.RefreshRequest;
import com.coworking.space.authenticationservice.dto.request.SingUpRequest;
import com.coworking.space.authenticationservice.dto.response.AuthResponse;
import com.coworking.space.authenticationservice.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

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

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterAndReturnValidJwtTokens() throws Exception {
        SingUpRequest request = new SingUpRequest("alex_dev", "password123");

        String responseJson = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn().getResponse().getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(responseJson, AuthResponse.class);

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
        SingUpRequest request = new SingUpRequest("duplicate_user", "password123");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldAuthenticateAndReturnTokens() throws Exception {
        SingUpRequest signUp = new SingUpRequest("john_doe", "secret_pass");
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUp)));

        LoginRequest login = new LoginRequest("john_doe", "secret_pass");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        SingUpRequest signUp = new SingUpRequest("refresh_user", "secret_pass");
        String signupResponse = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp)))
                .andReturn().getResponse().getContentAsString();

        AuthResponse initialTokens = objectMapper.readValue(signupResponse, AuthResponse.class);

        RefreshRequest refreshRequest = new RefreshRequest(initialTokens.getRefreshToken());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void shouldRejectRefreshWhenAccessTokenProvidedInsteadOfRefreshToken() throws Exception {
        SingUpRequest signUp = new SingUpRequest("hacker_user", "secret_pass");
        String signupResponse = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp)))
                .andReturn().getResponse().getContentAsString();

        AuthResponse tokens = objectMapper.readValue(signupResponse, AuthResponse.class);

        RefreshRequest invalidRefreshRequest = new RefreshRequest(tokens.getAccessToken());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRefreshRequest)))
                .andExpect(status().isUnauthorized());
    }
}
