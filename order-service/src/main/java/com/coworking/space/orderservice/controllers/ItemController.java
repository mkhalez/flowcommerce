package com.coworking.space.orderservice.controllers;

import com.coworking.space.orderservice.dto.request.CreateItemRequest;
import com.coworking.space.orderservice.dto.request.UpdateItemRequest;
import com.coworking.space.orderservice.dto.response.ItemResponse;
import com.coworking.space.orderservice.services.ItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("api/item")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    private static final String LOCATION_OF_CREATED_RESOURCE_PATTERN = "/api/item/{id}";

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestBody @Valid CreateItemRequest request, UriComponentsBuilder builder) {
        var response = itemService.createItem(request);
        var location = builder.path(LOCATION_OF_CREATED_RESOURCE_PATTERN).buildAndExpand(response.getId()).toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> findById(@PathVariable @Positive int id) {
        var response = itemService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponse> updateById(@PathVariable @Positive int id, @RequestBody UpdateItemRequest request) {
        var response = itemService.updateById(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable @Positive int id) {
        itemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
