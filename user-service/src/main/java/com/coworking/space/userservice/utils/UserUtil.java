package com.coworking.space.userservice.utils;

import com.coworking.space.userservice.exception.UserNotFoundException;
import com.coworking.space.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final UserRepository userRepo;

    private static final String USER_NOT_FOUND_ERROR = "user not found";

    public String getAuthIdByUserId(int userId) {
        var entity = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ERROR));

        return entity.getAuthUserId();
    }

    public int getUserIdByAuthId(String authId) {
        var entity =  userRepo.findByAuthId(authId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ERROR));

        return entity.getId();
    }

    public String getAuthIdByEmail(String email) {
        var entity = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ERROR));

        return entity.getAuthUserId();
    }
}
