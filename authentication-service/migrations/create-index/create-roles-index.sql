--liquibase formatted sql
--changeset mkh_alez:init-roles-index

CREATE INDEX name_roles_index ON roles(name);