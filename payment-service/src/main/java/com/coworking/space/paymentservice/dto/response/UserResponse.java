package com.coworking.space.paymentservice.dto.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private int id;
    private String name;
    private String surname;
    private LocalDate birthDay;
    private String email;
    private int cardsCount;
    private boolean active;
}
