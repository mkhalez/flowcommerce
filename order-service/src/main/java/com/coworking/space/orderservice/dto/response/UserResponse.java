package com.coworking.space.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponse {
    private int id;
    private String name;
    private String surname;
    private LocalDate birthDay;
    private String email;
    private int cardsCount;
    private boolean active;
}
