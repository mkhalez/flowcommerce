package com.coworking.space.authenticationservice.services;

public interface UserService {
    void changeUserStatus(int id, boolean status);

    void deleteUserById(int id);
}
