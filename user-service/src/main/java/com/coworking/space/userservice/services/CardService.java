package com.coworking.space.userservice.services;

import com.coworking.space.userservice.dto.requests.CardCreateRequest;
import com.coworking.space.userservice.dto.requests.CardUpdateRequest;
import com.coworking.space.userservice.dto.responses.CardResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CardService {
    CardResponse createCard(CardCreateRequest request);

    CardResponse findById(int id);

    Page<CardResponse> findAll(int limit, int page, String holder);

    List<CardResponse> findByUserId(int userId);

    CardResponse updateCard(int id, CardUpdateRequest request);

    void changeStatus(int id, boolean status);

    void deleteById(int id);
}
