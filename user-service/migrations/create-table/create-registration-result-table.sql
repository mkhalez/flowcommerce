--liquibase formatted sql
--changeset mkh_alez:init-registration-result-table

CREATE TABLE registration_result(
     id UUID PRIMARY KEY,
     created_at TIMESTAMP WITH TIME ZONE NOT NULL
);