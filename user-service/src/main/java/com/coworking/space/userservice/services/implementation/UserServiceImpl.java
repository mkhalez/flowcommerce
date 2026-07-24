package com.coworking.space.userservice.services.implementation;

import com.coworking.space.userservice.domain.entities.UserEntity;
import com.coworking.space.userservice.dto.requests.UserCreateRequest;
import com.coworking.space.userservice.dto.requests.UserUpdateRequest;
import com.coworking.space.userservice.dto.responses.UserResponse;
import com.coworking.space.userservice.exception.UserNotFoundException;
import com.coworking.space.userservice.mapper.UserMapper;
import com.coworking.space.userservice.repositories.CardRepository;
import com.coworking.space.userservice.repositories.UserRepository;
import com.coworking.space.userservice.repositories.projection.UserCardsCount;
import com.coworking.space.userservice.repositories.specification.UserSpecification;
import com.coworking.space.userservice.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final CardRepository cardRepo;
    private final UserMapper userMapper;
    private final UserSpecification userSpecification;

    private static final int ZERO_CARD = 0;
    private static final String SORTING_BY_ID = "id";
    private static final String USER_NOT_FOUND_ERROR = "user not found";

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        var entity = userMapper.toUserEntity(request);
        var saved = userRepo.save(entity);

        return userMapper.toUserResponse(saved, ZERO_CARD);
    }

    @Override
    public UserResponse getUserById(int id) {
        var entity = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ERROR));
        int cardsCount = cardRepo.countByUserId(id);

        return userMapper.toUserResponse(entity, cardsCount);
    }

    @Override
    public Page<UserResponse> findAll(int limit, int pageNum, String name, String surname) {
        Pageable page = PageRequest.of(pageNum, limit, Sort.by(SORTING_BY_ID));
        Specification<UserEntity> spec = Specification.<UserEntity>unrestricted()
                .and(userSpecification.hasName(name))
                .and(userSpecification.hasSurname(surname));

        var entities = userRepo.findAll(spec, page);

        List<Integer> ids = entities.getContent().stream()
                .map(UserEntity::getId)
                .toList();

        Map<Integer, Integer> countsByUserId = ids.isEmpty()
                ? Map.of()
                : cardRepo.getUserCardsCount(ids).stream()
                    .collect(Collectors.toMap(UserCardsCount::getUserId, UserCardsCount::getCount));


        return entities.map(entity -> {
            int cardsCount = countsByUserId.getOrDefault(entity.getId(), ZERO_CARD);
            return userMapper.toUserResponse(entity, cardsCount);
        });
    }

    @Override
    public UserResponse updateUser(int id, UserUpdateRequest request) {
        var entity = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ERROR));
        userMapper.updateUserEntity(request, entity);

        var saved = userRepo.save(entity);
        int count = cardRepo.countByUserId(saved.getId());

        return userMapper.toUserResponse(saved, count);
    }

    @Override
    @Transactional
    public void changeStatus(int id, boolean status) {
        userRepo.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ERROR));

        userRepo.updateStatusById(id, status);

        if(!status) {
            cardRepo.updateAllStatusById(id, status);
        }
    }
}
