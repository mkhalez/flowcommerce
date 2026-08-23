package com.coworking.space.userservice.services;

import com.coworking.space.userservice.dto.requests.*;
import com.coworking.space.userservice.dto.responses.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreateRequest request, String authUserId);

    UserResponse getUserById(int id);

    Page<UserResponse> findAll(int limit, int page, String name, String surname);

    UserResponse updateUser(int id, UserUpdateRequest request);

    void changeStatus(int id, boolean status);

    void deleteById(int id);

    UserResponse findByEmail(String email);
}
