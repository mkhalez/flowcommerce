--liquibase formatted sql
--changeset mkh_alez:init-order-items-table

CREATE TABLE order_items(
    id int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id NOT NULL references orders(id),
    item_id NOT NULL references items(id),
    quantity int NOT NULL,
    deleted bool NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL
);