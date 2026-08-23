--liquibase formatted sql
--changeset mkh_alez:init-orders-indexes

CREATE INDEX user_id_orders_index ON orders(user_id);
CREATE INDEX status_orders_index ON orders(status);
CREATE INDEX created_at__orders_index ON orders(created_at);
