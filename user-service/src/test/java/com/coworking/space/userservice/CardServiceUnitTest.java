package com.coworking.space.userservice;

import com.coworking.space.userservice.domain.entities.UserEntity;
import com.coworking.space.userservice.dto.requests.CardCreateRequest;
import com.coworking.space.userservice.exception.ExceededLimitException;
import com.coworking.space.userservice.mapper.CardMapper;
import com.coworking.space.userservice.repositories.CardRepository;
import com.coworking.space.userservice.repositories.UserRepository;
import com.coworking.space.userservice.repositories.specification.CardSpecification;
import com.coworking.space.userservice.services.implementation.CardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceUnitTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private CardRepository cardRepo;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CardSpecification cardSpecification;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void checkExceptionIfUserHasMoreThanFiveCard() {
        int userId = 1;
        CardCreateRequest request = new CardCreateRequest(
                userId,
                "1234567812345678",
                "PASHA IVANOV",
                LocalDate.of(2028, 12, 31)
        );

        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(cardRepo.countByUserId(userId)).thenReturn(5);

        ExceededLimitException exception = assertThrows(
                ExceededLimitException.class,
                () -> cardService.createCard(request)
        );

        assertEquals("user can not have more than 5 card", exception.getMessage());
        verify(cardRepo, never()).save(any());
    }

}
