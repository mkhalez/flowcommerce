package com.coworking.space.authenticationservice.dto.response;

import com.coworking.space.authenticationservice.domain.statuses.RegistrationEventStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationStatusResponse {
    private UUID transactionId;

    private RegistrationEventStatus status;
}
