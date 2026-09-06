--liquibase formatted sql
--changeset mkh_alez:init-registration-event-table

CREATE TABLE registration_event(
    id UUID PRIMARY KEY,
    user_id int NOT NULL references users(id),
    payload text NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status TEXT NOT NULL,
    attempt_count int NOT NULL,
    next_attempt_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sending_started_at TIMESTAMP WITH TIME ZONE
);