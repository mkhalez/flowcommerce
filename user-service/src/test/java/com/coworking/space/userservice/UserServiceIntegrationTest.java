package com.coworking.space.userservice;

import com.coworking.space.userservice.dto.requests.CardCreateRequest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class UserServiceIntegrationTest {
    private static final String USER_ROUTE = "/api/users";
    private static final String CLEAN_USER_TABLE = "DELETE FROM users";
    private static final String CARD_ROUTE = "/api/cards";
    private static final String CLEAN_CARD_TABLE = "DELETE FROM payment_cards";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanDB() {
        jdbcTemplate.update(CLEAN_CARD_TABLE);
        jdbcTemplate.update(CLEAN_USER_TABLE);
    }

    @Test
    void createUserTest() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru"
        );



        String responseJson = mockMvc.perform(post(USER_ROUTE).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserResponse actual = mapper.readValue(responseJson, UserResponse.class);
        UserResponse expect = new UserResponse(
                actual.id(), "Pasha", "Ivanov", LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru", 0, true
        );
        Assertions.assertEquals(expect, actual);
    }

    @Test
    void getUserByIdTest() throws Exception {
        UserCreateRequest requestToCreate = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru"
        );

        String response = mockMvc.perform(post(USER_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestToCreate)))
                .andReturn().getResponse().getContentAsString();

        int id = mapper.readValue(response, UserResponse.class).id();
        UserResponse responseById = new UserResponse(
                id, "Pasha", "Ivanov", LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru", 0, true
        );



        mockMvc.perform(get(USER_ROUTE + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseById)));
    }

    @Test
    void getUserByIdThatNotExistTest() throws Exception {
        ErrorResponse expect = new ErrorResponse("user not found");
        int id = 10;

        mockMvc.perform(get(USER_ROUTE + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
    }

    @Test
    void getUsers() throws Exception {
        UserCreateRequest requestToCreate1 = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "pasha@mail.ru"
        );
        String response1 = mockMvc.perform(post(USER_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestToCreate1)))
                .andReturn().getResponse().getContentAsString();
        int id1 = mapper.readValue(response1, UserResponse.class).id();
        UserResponse responseById1 = new UserResponse(
                id1, "Pasha", "Ivanov", LocalDate.of(1995, 6, 15),
                "pasha@mail.ru", 0, true
        );

        UserCreateRequest requestToCreate2 = new UserCreateRequest(
                "Sasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "sasha@mail.ru"
        );
        String response = mockMvc.perform(post(USER_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestToCreate2)))
                .andReturn().getResponse().getContentAsString();
        int id2 = mapper.readValue(response, UserResponse.class).id();
        UserResponse responseById2 = new UserResponse(
                id2, "Sasha", "Ivanov", LocalDate.of(1995, 6, 15),
                "sasha@mail.ru", 0, true
        );


        UserCreateRequest requestToCreate3 = new UserCreateRequest(
                "Pasha",
                "Lopuhov",
                LocalDate.of(1995, 6, 15),
                "Lopuhov@mail.ru"
        );

        mockMvc.perform(post(USER_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestToCreate3)));

        String pageJsonResponse = mockMvc.perform(get(USER_ROUTE)
                        .param("limit", "5")
                        .param("page", "0")
                        .param("surname", "Ivanov"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = mapper.readTree(pageJsonResponse);

        List<UserResponse> actual = mapper.convertValue(
                root.get("content"),
                mapper.getTypeFactory().constructCollectionType(List.class, UserResponse.class));

        Assertions.assertEquals(2, actual.size());
        Assertions.assertTrue(actual.contains(responseById1));
        Assertions.assertTrue(actual.contains(responseById2));

    }

    @Test
    void updateUserTest() throws Exception {
        UserCreateRequest requestToCreate = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru"
        );

        UserUpdateRequest requestToUpdate = new UserUpdateRequest(
                "Misha",
                null,
                null,
                "pasha228@gmail.com"
        );

        String response = mockMvc.perform(post(USER_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestToCreate)))
                .andReturn().getResponse().getContentAsString();

        int id = mapper.readValue(response, UserResponse.class).id();

        UserResponse responseById = new UserResponse(
                id, "Misha", "Ivanov", LocalDate.of(1995, 6, 15),
                "pasha228@gmail.com", 0, true
        );

        mockMvc.perform(patch(USER_ROUTE + "/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(requestToUpdate)));

        mockMvc.perform(get(USER_ROUTE + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseById)));
    }

    @Test
    void deactivateUserAndAllHisCardsTest() throws Exception {
        UserCreateRequest requestToCreate = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru"
        );

        String response = mockMvc.perform(post(USER_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestToCreate)))
                .andReturn().getResponse().getContentAsString();

        int id = mapper.readValue(response, UserResponse.class).id();

        UserResponse responseById = new UserResponse(
                id, "Pasha", "Ivanov", LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru", 3, false
        );

        CardCreateRequest cardCreateRequest1 = new CardCreateRequest(
                id,
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest2 = new CardCreateRequest(
                id,
                "1234167812341652",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        CardCreateRequest cardCreateRequest3 = new CardCreateRequest(
                id,
                "1234167812341653",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );


        mockMvc.perform(post(CARD_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest1)))
                .andExpect(status().isCreated());
        mockMvc.perform(post(CARD_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest2)))
                .andExpect(status().isCreated());
        mockMvc.perform(post(CARD_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest3)))
                .andExpect(status().isCreated());

        mockMvc.perform(patch(USER_ROUTE + "/" + id + "/deactivate"));

        String cardsJson = mockMvc.perform(get(CARD_ROUTE + "/by-user/" + id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<CardResponse> cards = mapper.readValue(
                cardsJson,
                new TypeReference<List<CardResponse>>() {}
        );

        mockMvc.perform(get(USER_ROUTE + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseById)));

        for(var card : cards) {
            Assertions.assertFalse(card.active());
        }


    }


    @Test
    void deleteUserTest() throws Exception {
        ErrorResponse expectErrorResponseByUser = new ErrorResponse("user not found");
        ErrorResponse expectErrorResponseByCard = new ErrorResponse("card not found");

        UserCreateRequest requestToCreate = new UserCreateRequest(
                "Pasha",
                "Ivanov",
                LocalDate.of(1995, 6, 15),
                "ivanov@mail.ru"
        );



        String response = mockMvc.perform(post(USER_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestToCreate)))
                .andReturn().getResponse().getContentAsString();

        UserResponse user = mapper.readValue(response, UserResponse.class);

        CardCreateRequest cardCreateRequest = new CardCreateRequest(
                user.id(),
                "1234167812341651",
                "PASHA IVANOV",
                LocalDate.of(2028, 2, 1)
        );

        String cardJsonResponse = mockMvc.perform(post(CARD_ROUTE).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cardCreateRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CardResponse actualCardResponse = mapper.readValue(cardJsonResponse, CardResponse.class);


        mockMvc.perform(delete(USER_ROUTE + "/" + user.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(USER_ROUTE + "/" + user.id()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expectErrorResponseByUser)));

        mockMvc.perform(get(CARD_ROUTE + "/" + actualCardResponse.id()))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expectErrorResponseByCard)));
    }
}