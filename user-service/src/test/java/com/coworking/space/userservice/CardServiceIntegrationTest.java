package com.coworking.space.userservice;

import com.coworking.space.userservice.dto.requests.CardCreateRequest;
import com.coworking.space.userservice.dto.requests.CardUpdateRequest;
import com.coworking.space.userservice.dto.requests.UserCreateRequest;
import com.coworking.space.userservice.dto.requests.UserUpdateRequest;
import com.coworking.space.userservice.dto.responses.CardResponse;
import com.coworking.space.userservice.dto.responses.ErrorResponse;
import com.coworking.space.userservice.dto.responses.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class CardServiceIntegrationTest {
    private static final String USER_ROUTE = "/api/user";
    private static final String USER_REGISTER_ROUTE = "/api/user/register";
    private static final String CARD_ROUTE = "/api/user/card";
    private static final String CLEAN_CARD_TABLE = "DELETE FROM payment_cards";
    private static final String CLEAN_USER_TABLE = "DELETE FROM users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @AfterEach
    void cleanDB() {
        jdbcTemplate.update(CLEAN_CARD_TABLE);
        jdbcTemplate.update(CLEAN_USER_TABLE);
    }

    @Test
    void createUserCardTest() throws Exception {
        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );

        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(responseJson, UserResponse.class);

        CardCreateRequest cardCreateRequest = new CardCreateRequest(
                user.id(),
                "1234167812341653",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        String jsonResponse = mockMvc.perform(post(CARD_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .content(mapper.writeValueAsString(cardCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CardResponse actual = mapper.readValue(jsonResponse, CardResponse.class);
        CardResponse expect = new CardResponse(
                actual.id(),
                user.id(),
                "1234167812341653",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1),
                true
        );

        Assertions.assertEquals(expect, actual);
    }

    @Test
    void createCardWithoutUserTest() throws Exception {
        ErrorResponse expectError = new ErrorResponse("user not found");
        CardCreateRequest cardCreateRequest = new CardCreateRequest(
                1,
                "1234167812341653",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expectError)));


    }

    @Test
    void createMoreThanFiveCardTest() throws Exception {
        ErrorResponse expectErrorResponse = new ErrorResponse("user can not have more than 5 card");

        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );
        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(responseJson, UserResponse.class);

        CardCreateRequest cardCreateRequest1 = new CardCreateRequest(
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest2 = new CardCreateRequest(
                user.id(),
                "1234167812341652",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest3 = new CardCreateRequest(
                user.id(),
                "1234167812341653",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest4 = new CardCreateRequest(
                user.id(),
                "1234167812341654",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest5 = new CardCreateRequest(
                user.id(),
                "1234167812341655",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest6 = new CardCreateRequest(
                user.id(),
                "1234167812341656",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest1)))
                .andExpect(status().isCreated());
        mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest2)))
                .andExpect(status().isCreated());
        mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest3)))
                .andExpect(status().isCreated());
        mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest4)))
                .andExpect(status().isCreated());
        mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest5)))
                .andExpect(status().isCreated());
        mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest6)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expectErrorResponse)));

    }

    @Test
    void getCardByIdTest() throws Exception {
        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );
        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(responseJson, UserResponse.class);

        CardCreateRequest cardCreateRequest = new CardCreateRequest(
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        String cardJsonResponse = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CardResponse actualCardResponse = mapper.readValue(cardJsonResponse, CardResponse.class);

        CardResponse expectCardResponse = new CardResponse(
                actualCardResponse.id(),
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1),
                true
        );

        String getCardResponseByIdJson = mockMvc.perform(get(CARD_ROUTE + "/" + actualCardResponse.id())
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CardResponse getCardResponseById = mapper.readValue(getCardResponseByIdJson, CardResponse.class);

        Assertions.assertEquals(expectCardResponse, getCardResponseById);
    }

    @Test
    void getCardByIdThatNotExistTest() throws Exception {
        ErrorResponse expectErrorResponse = new ErrorResponse("card not found");

        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );
        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get(CARD_ROUTE + "/1")
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expectErrorResponse)));
    }

    @Test
    void getCards() throws Exception {
        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );
        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(responseJson, UserResponse.class);

        CardCreateRequest cardCreateRequest1 = new CardCreateRequest(
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest2 = new CardCreateRequest(
                user.id(),
                "1234167812341652",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest3 = new CardCreateRequest(
                user.id(),
                "1234167812341653",
                "PASHA PETROV",
                LocalDate.of(2028, 2, 1)
        );


        String card1Json = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        CardResponse cardResponse1 = mapper.readValue(card1Json, CardResponse.class);

        String card2Json = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest2)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        CardResponse cardResponse2 = mapper.readValue(card2Json, CardResponse.class);


        mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest3)))
                .andExpect(status().isCreated());


        String cardsJson = mockMvc.perform(get(CARD_ROUTE)
                        .param("limit", "10")
                        .param("page", "0")
                        .param("holder", "PASHA IVANOV")
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = mapper.readTree(cardsJson);

        List<CardResponse> actual = mapper.convertValue(
                root.get("content"),
                mapper.getTypeFactory().constructCollectionType(List.class, CardResponse.class));

        Assertions.assertEquals(2, actual.size());
        Assertions.assertTrue(actual.contains(cardResponse1));
        Assertions.assertTrue(actual.contains(cardResponse2));
    }

    @Test
    void getCardsByUserIdTest() throws Exception {
        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );
        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(responseJson, UserResponse.class);

        CardCreateRequest cardCreateRequest1 = new CardCreateRequest(
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest2 = new CardCreateRequest(
                user.id(),
                "1234167812341652",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest3 = new CardCreateRequest(
                user.id(),
                "1234167812341653",
                "PASHA PETROV",
                LocalDate.of(2028, 2, 1)
        );


        String card1Json = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        CardResponse cardResponse1 = mapper.readValue(card1Json, CardResponse.class);

        String card2Json = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest2)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        CardResponse cardResponse2 = mapper.readValue(card2Json, CardResponse.class);


        String card3Json = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest3)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        CardResponse cardResponse3 = mapper.readValue(card3Json, CardResponse.class);

        String cardsJson = mockMvc.perform(get(CARD_ROUTE + "/by-user/" + user.id())
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<CardResponse> cards = mapper.readValue(
                cardsJson,
                new TypeReference<List<CardResponse>>() {}
        );

        Assertions.assertEquals(3, cards.size());
        Assertions.assertTrue(cards.contains(cardResponse1));
        Assertions.assertTrue(cards.contains(cardResponse2));
        Assertions.assertTrue(cards.contains(cardResponse3));
    }

    @Test
    void updateUserTest() throws Exception {
        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );
        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(responseJson, UserResponse.class);

        CardCreateRequest cardCreateRequest1 = new CardCreateRequest(
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        String card1Json = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        CardResponse cardResponse1 = mapper.readValue(card1Json, CardResponse.class);

        CardUpdateRequest cardUpdateRequest = new CardUpdateRequest(
                "MASHA IVANOV",
                null
        );

        String cardResponseJson = mockMvc.perform(patch(CARD_ROUTE + "/" + cardResponse1.id())
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardUpdateRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CardResponse actualResponse = mapper.readValue(cardResponseJson, CardResponse.class);
        CardResponse expectResponse= new CardResponse(
                actualResponse.id(),
                user.id(),
                "1234167812341651",
                "MASHA IVANOV",
                LocalDate.of(2028, 2, 1),
                true
        );

        Assertions.assertEquals(expectResponse, actualResponse);
    }

    @Test
    void deactivateCardTest() throws Exception {
        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );
        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(responseJson, UserResponse.class);

        CardCreateRequest cardCreateRequest = new CardCreateRequest(
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        String cardJsonResponse = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CardResponse actualCardResponse = mapper.readValue(cardJsonResponse, CardResponse.class);

        mockMvc.perform(patch(CARD_ROUTE + "/" + actualCardResponse.id() + "/deactivate")
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());

        CardResponse expectCardResponse = new CardResponse(
                actualCardResponse.id(),
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1),
                false
        );

        String getCardResponseByIdJson = mockMvc.perform(get(CARD_ROUTE + "/" + actualCardResponse.id())
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CardResponse getCardResponseById = mapper.readValue(getCardResponseByIdJson, CardResponse.class);

        Assertions.assertEquals(expectCardResponse, getCardResponseById);
    }

    @Test
    void deleteCardTest() throws Exception {
        ErrorResponse expectErrorResponse = new ErrorResponse("card not found");
        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );
        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(responseJson, UserResponse.class);

        CardCreateRequest cardCreateRequest = new CardCreateRequest(
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        String cardJsonResponse = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CardResponse actualCardResponse = mapper.readValue(cardJsonResponse, CardResponse.class);

        mockMvc.perform(delete(CARD_ROUTE + "/" + actualCardResponse.id())
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(CARD_ROUTE + "/" + actualCardResponse.id())
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expectErrorResponse)));

    }

    @Test
    void userDeleteCardTest() throws Exception {
        ErrorResponse expectErrorResponse = new ErrorResponse("card not found");
        UserCreateRequest userRequest = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru",
                "1"
        );
        String responseJson = mockMvc.perform(post(USER_REGISTER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(responseJson, UserResponse.class);

        CardCreateRequest cardCreateRequest = new CardCreateRequest(
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        String cardJsonResponse = mockMvc.perform(post(CARD_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CardResponse actualCardResponse = mapper.readValue(cardJsonResponse, CardResponse.class);

        mockMvc.perform(delete(CARD_ROUTE + "/" + actualCardResponse.id())
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());

    }
}