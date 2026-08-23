package com.coworking.space.orderservice.services;

import com.coworking.space.orderservice.dto.request.CreateItemRequest;
import com.coworking.space.orderservice.dto.request.UpdateItemRequest;
import com.coworking.space.orderservice.dto.response.ItemResponse;

public interface ItemService {
    ItemResponse createItem(CreateItemRequest request);

    ItemResponse findById(int id);

    ItemResponse updateById(int id, UpdateItemRequest request);

    void deleteById(int id);
}
