package com.coworking.space.orderservice.dto.request;

import com.coworking.space.orderservice.domain.enums.Status;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderFilterParams {
    private OffsetDateTime from;

    private OffsetDateTime to;

    private Status status;

    @Email
    private String email;
}
