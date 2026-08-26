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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.claims['userId'].toString().equals(@userUtil.getAuthIdByUserId(#request.userId))")
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
        var response = cardService.createCard(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.claims['userId'].toString().equals(@cardUtil.getAuthIdOfCardOwner(#id))")
    public ResponseEntity<CardResponse> getCardById(@PathVariable int id) {
        var response = cardService.findById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardResponse>> getCards(
            @RequestParam(required = false) String holder,
            @RequestParam int limit,
            @RequestParam int page) {
        var response = cardService.findAll(limit, page, holder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @userUtil.getAuthIdByUserId(#userId).equals(authentication.principal.claims['userId'].toString())")
    public ResponseEntity<List<CardResponse>> getCardsByUserId(@PathVariable int userId) {
        var response = cardService.findByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.claims['userId'].toString().equals(@cardUtil.getAuthIdOfCardOwner(#id))")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable int id,
            @Valid @RequestBody CardUpdateRequest request) {
        var response = cardService.updateCard(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public void activateCard(@PathVariable int id) {
        cardService.changeStatus(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateCard(@PathVariable int id) {
        cardService.changeStatus(id, false);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable int id) {
        cardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
