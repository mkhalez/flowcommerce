--liquibase formatted sql
--changeset mkh_alez:init-users-table
CREATE TABLE users(
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name text NOT NULL,
    surname text NOT NULL,
    birth_date DATE NOT NULL,
    email text UNIQUE NOT NULL,
    active bool NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);