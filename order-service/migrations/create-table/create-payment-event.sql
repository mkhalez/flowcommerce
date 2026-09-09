--liquibase formatted sql
--changeset mkh_alez:init-payment-event-table

CREATE TABLE payment_event(
    id text PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);