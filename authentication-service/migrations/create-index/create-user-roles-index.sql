--liquibase formatted sql
--changeset mkh_alez:init-user-roles-table

CREATE INDEX role_id_user_roles_index ON user_roles(role_id);