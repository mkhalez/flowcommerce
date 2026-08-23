package com.coworking.space.orderservice.dto.request;

import com.coworking.space.orderservice.domain.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateOrderStatusRequest {
    @NotNull
    private Status status;
}
