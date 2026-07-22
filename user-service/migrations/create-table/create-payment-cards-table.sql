--liquibase formatted sql
--changeset mkh_alez:init-payment-cards-table
CREATE TABLE payment_cards(
    id int PRIMARY KEY,
    user_id int references users(id),
    number text NOT NULL UNIQUE,
    holder text NOT NULL ,
    expiration_date DATE NOT NULL ,
    active bool NOT NULL
);