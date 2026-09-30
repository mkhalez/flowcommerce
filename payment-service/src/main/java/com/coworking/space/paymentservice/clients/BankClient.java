package com.coworking.space.paymentservice.clients;

import com.coworking.space.paymentservice.dto.response.RandomOrgResponse;
import com.coworking.space.paymentservice.infrastructure.properties.BankClientProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface BankClient {
    @PostExchange
    RandomOrgResponse getRandomNumber(@RequestBody BankClientProperties.Body body);
}
