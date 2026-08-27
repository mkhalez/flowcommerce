package com.coworking.space.userservice.dto.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserCreateRequest {
        private @NotBlank String name;
        private @NotBlank String surname;
        private @NotNull @Past LocalDate birthDay;
        private @Email String email;
        private @NotBlank String authId;
}
