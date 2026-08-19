package com.coworking.space.orderservice.services.implementation;

import com.coworking.space.orderservice.domain.exceptions.ItemNotFoundException;
import com.coworking.space.orderservice.dto.request.CreateItemRequest;
import com.coworking.space.orderservice.dto.request.UpdateItemRequest;
import com.coworking.space.orderservice.dto.response.ItemResponse;
import com.coworking.space.orderservice.mappers.ItemMapper;
import com.coworking.space.orderservice.repositories.ItemRepository;
import com.coworking.space.orderservice.services.ItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepo;

    private static final String ITEM_NOT_FOUND_ERROR = "item not found";

    @Override
    public ItemResponse createItem(CreateItemRequest request) {
        var entity = itemMapper.toEntity(request);
        entity = itemRepo.save(entity);
        return itemMapper.toResponse(entity);
    }

    @Override
    public ItemResponse findById(int id) {
        var entity = itemRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND_ERROR));
        return itemMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public ItemResponse updateById(int id, UpdateItemRequest request) {
        var entity = itemRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND_ERROR));

        itemMapper.updateEntity(request, entity);
        return itemMapper.toResponse(entity);
    }

    @Override
    public void deleteById(int id) {
        if(!itemRepo.existsById(id)) {
            throw new ItemNotFoundException(ITEM_NOT_FOUND_ERROR);
        }

        itemRepo.deleteById(id);
    }

}
