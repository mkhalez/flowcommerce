--liquibase formatted sql
--changeset mkh_alez:init-registration-processed-event-table

CREATE TABLE registration_processed_event(
     id UUID PRIMARY KEY,
     payload text NOT NULL,
     created_at TIMESTAMP WITH TIME ZONE NOT NULL,
     last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
     status TEXT NOT NULL,
     attempt_count int NOT NULL,
     next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL,
     sending_started_at TIMESTAMP WITH TIME ZONE
);