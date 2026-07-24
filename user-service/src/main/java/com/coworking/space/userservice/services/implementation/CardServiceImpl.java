package com.coworking.space.userservice.services.implementation;

import com.coworking.space.userservice.domain.entities.CardEntity;
import com.coworking.space.userservice.dto.requests.CardCreateRequest;
import com.coworking.space.userservice.dto.requests.CardUpdateRequest;
import com.coworking.space.userservice.dto.responses.CardResponse;
import com.coworking.space.userservice.exception.CardNotFoundException;
import com.coworking.space.userservice.exception.ExceededLimitException;
import com.coworking.space.userservice.exception.UserNotFoundException;
import com.coworking.space.userservice.mapper.CardMapper;
import com.coworking.space.userservice.repositories.CardRepository;
import com.coworking.space.userservice.repositories.UserRepository;
import com.coworking.space.userservice.repositories.specification.CardSpecification;
import com.coworking.space.userservice.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final UserRepository userRepo;
    private final CardRepository cardRepo;
    private final CardMapper cardMapper;

    private static final String USER_NOT_FOUND_ERROR = "user not found";
    private static final String CARD_NOT_FOUND_ERROR = "card not found";
    private static final String USER_CAN_NOT_HAVE_MORE_THAN_FIVE_CARD = "user can not have more than 5 card";
    private static final int MAX_CARD_NUMBER = 5;
    private static final String SORTING_BY_ID = "id";

    @Override
    public CardResponse createCard(CardCreateRequest request) {
        var userEntity = userRepo.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ERROR));

        int cardCount = cardRepo.countByUserId(request.userId());

        if (cardCount > MAX_CARD_NUMBER) {
            throw new ExceededLimitException(USER_CAN_NOT_HAVE_MORE_THAN_FIVE_CARD);
        }

        var cardEntity = cardMapper.toCardEntity(request, userEntity);
        var savedCardEntity = cardRepo.save(cardEntity);

        return cardMapper.toCardResponse(savedCardEntity, request.userId());
    }

    @Override
    public CardResponse findById(int id) {
        var cardEntity = cardRepo.findById(id)
                .orElseThrow(() -> new CardNotFoundException(CARD_NOT_FOUND_ERROR));

        return cardMapper.toCardResponse(cardEntity, cardEntity.getUser().getId());
    }

    @Override
    public Page<CardResponse> findAll(int limit, int pageNum, String holder) {
        Pageable page = PageRequest.of(pageNum, limit, Sort.by(SORTING_BY_ID));
        Specification<CardEntity> spec = Specification.<CardEntity>unrestricted()
                .and(CardSpecification.hasHolder(holder));

        var entities = cardRepo.findAll(spec, page);

        return entities.map(entity -> cardMapper.toCardResponse(entity, entity.getUser().getId()));
    }

    @Override
    public List<CardResponse> findByUserId(int userId) {
        var cards = cardRepo.findByUserId(userId);

        return cards.stream()
                .map(card -> cardMapper.toCardResponse(card, userId))
                .toList();
    }

    @Override
    public CardResponse updateCard(int id, CardUpdateRequest request) {
        var entity = cardRepo.findById(id)
                .orElseThrow(() -> new CardNotFoundException(CARD_NOT_FOUND_ERROR));

        cardMapper.updateCardEntity(request, entity);
        var saved = cardRepo.save(entity);

        return cardMapper.toCardResponse(saved, saved.getUser().getId());
    }

    @Override
    public void changeStatus(int id, boolean status) {
        cardRepo.findById(id)
                .orElseThrow(() -> new CardNotFoundException(CARD_NOT_FOUND_ERROR));

        cardRepo.updateStatusById(id);
    }
}
