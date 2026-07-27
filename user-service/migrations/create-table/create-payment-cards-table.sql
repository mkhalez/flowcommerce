--liquibase formatted sql
--changeset mkh_alez:init-payment-cards-table
CREATE TABLE payment_cards(
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id int references users(id) NOT NULL ,
    number text NOT NULL UNIQUE,
    holder text NOT NULL ,
    expiration_date DATE NOT NULL ,
    active bool NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);