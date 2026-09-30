--liquibase formatted sql
--changeset mkh_alez:init-rollback_result-table

CREATE TABLE rollback_result(
    id UUID PRIMARY KEY,
    message text NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);