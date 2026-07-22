package com.coworking.space.userservice.controllers;

import com.coworking.space.userservice.dto.requests.CardCreateRequest;
import com.coworking.space.userservice.dto.requests.CardUpdateRequest;
import com.coworking.space.userservice.dto.responses.CardResponse;
import com.coworking.space.userservice.services.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
        var response = cardService.createCard(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable int id) {
        var response = cardService.findById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<CardResponse>> getCards(
            @RequestParam(required = false) String holder,
            @RequestParam int limit,
            @RequestParam int page) {
        var response = cardService.findAll(limit, page, holder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<CardResponse>> getCardsByUserId(@PathVariable int userId) {
        var response = cardService.findByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable int id,
            @Valid @RequestBody CardUpdateRequest request) {
        var response = cardService.updateCard(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/activate")
    public void activateCard(@PathVariable int id) {
        cardService.changeStatus(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public void deactivateCard(@PathVariable int id) {
        cardService.changeStatus(id, false);
    }
}
