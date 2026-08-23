package com.coworking.space.orderservice;

import com.coworking.space.orderservice.dto.request.CreateItemRequest;
import com.coworking.space.orderservice.dto.request.UpdateItemRequest;
import com.coworking.space.orderservice.dto.response.ErrorResponse;
import com.coworking.space.orderservice.dto.response.ItemResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class ItemControllerIntegrationTest {
    private static final String ITEM_ROUTE = "/api/item";
    private static final String CLEAN_ITEM_TABLE = "DELETE FROM items";

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanDB() {
        jdbcTemplate.update(CLEAN_ITEM_TABLE);
    }

    @Test
    void createItemAsAdminTest() throws Exception {
        CreateItemRequest request = new CreateItemRequest("Coffee", 5.50);

        String responseJson = mockMvc.perform(post(ITEM_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ItemResponse actual = mapper.readValue(responseJson, ItemResponse.class);

        Assertions.assertEquals("Coffee", actual.getName());
        Assertions.assertEquals(5.50, actual.getPrice(), 0.001);
    }

    @Test
    void createItemAsUserForbiddenTest() throws Exception {
        CreateItemRequest request = new CreateItemRequest("Tea", 3.00);

        mockMvc.perform(post(ITEM_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createItemInvalidPriceTest() throws Exception {
        CreateItemRequest request = new CreateItemRequest("Cake", -1);

        mockMvc.perform(post(ITEM_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByIdTest() throws Exception {
        CreateItemRequest request = new CreateItemRequest("Latte", 4.20);

        String createResponse = mockMvc.perform(post(ITEM_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        int id = mapper.readValue(createResponse, ItemResponse.class).getId();

        mockMvc.perform(get(ITEM_ROUTE + "/" + id)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Latte"));
    }

    @Test
    void findByIdNotFoundTest() throws Exception {
        ErrorResponse expect = new ErrorResponse("item not found");

        mockMvc.perform(get(ITEM_ROUTE + "/999")
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
    }

    @Test
    void updateByIdTest() throws Exception {
        CreateItemRequest createRequest = new CreateItemRequest("Espresso", 2.50);

        String createResponse = mockMvc.perform(post(ITEM_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        int id = mapper.readValue(createResponse, ItemResponse.class).getId();

        UpdateItemRequest updateRequest = new UpdateItemRequest("Double Espresso", 3.50);

        mockMvc.perform(patch(ITEM_ROUTE + "/" + id)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Double Espresso"))
                .andExpect(jsonPath("$.price").value(3.50));
    }

    @Test
    void deleteByIdTest() throws Exception {
        CreateItemRequest createRequest = new CreateItemRequest("Muffin", 1.80);

        String createResponse = mockMvc.perform(post(ITEM_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        int id = mapper.readValue(createResponse, ItemResponse.class).getId();

        mockMvc.perform(delete(ITEM_ROUTE + "/" + id)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(ITEM_ROUTE + "/" + id)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound());
    }
}