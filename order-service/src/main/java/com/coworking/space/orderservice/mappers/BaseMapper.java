package com.coworking.space.orderservice.mappers;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public interface BaseMapper {
    default OffsetDateTime convertToOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }
}
