package com.coworking.space.userservice.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserUpdateRequest(String name,
                                String surname,
                                @Past LocalDate birthDay,
                                @Email String email) {}
