package com.coworking.space.userservice.utils;

import com.coworking.space.userservice.exception.CardNotFoundException;
import com.coworking.space.userservice.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardUtil {
    private final CardRepository cardRepo;

    private static final String CARD_NOT_FOUND_ERROR = "card not found";

    public String getAuthIdOfCardOwner(int cardId) {
        var cardEntity = cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(CARD_NOT_FOUND_ERROR));

        return cardEntity.getUser().getAuthUserId();
    }
}
