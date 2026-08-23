--liquibase formatted sql
--changeset mkh_alez:init-orders-table

CREATE TABLE orders(
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id int NOT NULL,
    status text NOT NULL,
    total_price double precision NOT NULL,
    deleted bool NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL
);
