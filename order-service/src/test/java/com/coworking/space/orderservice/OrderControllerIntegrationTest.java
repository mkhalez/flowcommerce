package com.coworking.space.orderservice;

import com.coworking.space.orderservice.clients.UserServiceClient;
import com.coworking.space.orderservice.domain.enums.Status;
import com.coworking.space.orderservice.dto.request.CreateItemRequest;
import com.coworking.space.orderservice.dto.request.OrderItemRequest;
import com.coworking.space.orderservice.dto.request.OrderRequest;
import com.coworking.space.orderservice.dto.request.UpdateOrderStatusRequest;
import com.coworking.space.orderservice.dto.response.ErrorResponse;
import com.coworking.space.orderservice.dto.response.ItemResponse;
import com.coworking.space.orderservice.dto.response.OrderResponse;
import com.coworking.space.orderservice.dto.response.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class OrderControllerIntegrationTest {
    private static final String ORDER_ROUTE = "/api/order";
    private static final String ITEM_ROUTE = "/api/order/item";
    private static final String CLEAN_ORDER_ITEMS_TABLE = "DELETE FROM order_items";
    private static final String CLEAN_ORDERS_TABLE = "DELETE FROM orders";
    private static final String CLEAN_ITEMS_TABLE = "DELETE FROM items";

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void mockUserService() {
        UserResponse user = UserResponse.builder()
                .id(1)
                .name("Pasha")
                .surname("Ivanov")
                .email("ivanov@mail.ru")
                .cardsCount(0)
                .active(true)
                .build();

        when(userServiceClient.findUserByEmail(anyString())).thenReturn(user);
        when(userServiceClient.findById(anyInt())).thenReturn(user);
    }

    @AfterEach
    void cleanDB() {
        jdbcTemplate.update(CLEAN_ORDER_ITEMS_TABLE);
        jdbcTemplate.update(CLEAN_ORDERS_TABLE);
        jdbcTemplate.update(CLEAN_ITEMS_TABLE);
    }

    private int createItem(String name, double price) throws Exception {
        CreateItemRequest request = new CreateItemRequest(name, price);

        String response = mockMvc.perform(post(ITEM_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, ItemResponse.class).getId();
    }

    @Test
    void createOrderTest() throws Exception {
        int itemId = createItem("Coffee", 5.00);

        OrderRequest request = new OrderRequest(
                List.of(new OrderItemRequest(itemId, 2)),
                "ivanov@mail.ru"
        );

        String responseJson = mockMvc.perform(post(ORDER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        OrderResponse actual = mapper.readValue(responseJson, OrderResponse.class);

        Assertions.assertEquals(Status.CREATED, actual.getStatus());
        Assertions.assertEquals(10.00, actual.getTotalPrice(), 0.001);
        Assertions.assertEquals("ivanov@mail.ru", actual.getUser().getEmail());
    }

    @Test
    void createOrderWithMissingItemTest() throws Exception {
        OrderRequest request = new OrderRequest(
                List.of(new OrderItemRequest(9999, 1)),
                "ivanov@mail.ru"
        );

        mockMvc.perform(post(ORDER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdTest() throws Exception {
        int itemId = createItem("Tea", 3.00);
        OrderRequest createRequest = new OrderRequest(
                List.of(new OrderItemRequest(itemId, 1)),
                "ivanov@mail.ru"
        );

        String createResponse = mockMvc.perform(post(ORDER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        int id = mapper.readValue(createResponse, OrderResponse.class).getId();

        mockMvc.perform(get(ORDER_ROUTE + "/" + id)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void findByIdNotFoundTest() throws Exception {
        ErrorResponse expect = new ErrorResponse("not found order");

        mockMvc.perform(get(ORDER_ROUTE + "/999")
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
    }

    @Test
    void updateStatusByIdAsAdminTest() throws Exception {
        int itemId = createItem("Cake", 6.00);
        OrderRequest createRequest = new OrderRequest(
                List.of(new OrderItemRequest(itemId, 1)),
                "ivanov@mail.ru"
        );

        String createResponse = mockMvc.perform(post(ORDER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        int id = mapper.readValue(createResponse, OrderResponse.class).getId();

        UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest(Status.RECEIVED);

        mockMvc.perform(patch(ORDER_ROUTE + "/status/" + id)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void updateStatusByIdAsUserForbiddenTest() throws Exception {
        UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest(Status.RECEIVED);

        mockMvc.perform(patch(ORDER_ROUTE + "/status/1")
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(statusRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteByIdTest() throws Exception {
        int itemId = createItem("Juice", 2.00);
        OrderRequest createRequest = new OrderRequest(
                List.of(new OrderItemRequest(itemId, 1)),
                "ivanov@mail.ru"
        );

        String createResponse = mockMvc.perform(post(ORDER_ROUTE)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        int id = mapper.readValue(createResponse, OrderResponse.class).getId();

        mockMvc.perform(delete(ORDER_ROUTE + "/" + id)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(ORDER_ROUTE + "/" + id)
                        .with(jwt().jwt(builder -> builder
                                        .claim("userId", 1)
                                        .claim("type", "access"))
                                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound());
    }
}