--liquibase formatted sql
--changeset mkh_alez:init-index-users-table
CREATE INDEX name_users_index ON users(name);
CREATE INDEX surname_users_index ON users(surname);
CREATE INDEX auth_user_id_users_index ON users(auth_user_id);