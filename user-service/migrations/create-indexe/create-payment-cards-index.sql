--liquibase formatted sql
--changeset mkh_alez:init-index-payment_cards-table
CREATE INDEX user_id_payment_cards_index ON payment_cards(user_id);
CREATE INDEX holder_payment_cards_index ON payment_cards(holder);
