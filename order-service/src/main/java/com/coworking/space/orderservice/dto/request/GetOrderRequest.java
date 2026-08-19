package com.coworking.space.orderservice.dto.request;

import com.coworking.space.orderservice.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderRequest {
    private OffsetDateTime from;

    private OffsetDateTime to;

    private Status status;
}
