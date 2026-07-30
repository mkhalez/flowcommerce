--liquibase formatted sql
--changeset mkh_alez:init-users-index

CREATE INDEX username_users_index ON users(username);