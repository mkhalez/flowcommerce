--liquibase formatted sql
--changeset mkh_alez:init-items-table

CREATE TABLE items (
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name text NOT NULL,
    price double precision NOT NULL,
    create_at created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);