package com.coworking.space.paymentservice.services.implementation;

import com.coworking.space.paymentservice.clients.BankClient;
import com.coworking.space.paymentservice.infrastructure.properties.BankClientProperties;
import com.coworking.space.paymentservice.services.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankServiceImpl implements BankService {
    private final BankClientProperties properties;
    private final BankClient client;

    public boolean isApproved() {
        try {
            var response = client.getRandomNumber(properties.getBody());
            return response.result().random().data().getFirst() % 2 == 0;
        } catch (Exception e) {
            log.atError().setCause(e).log();
            return false;
        }

    }

}
