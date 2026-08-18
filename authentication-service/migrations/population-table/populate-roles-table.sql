--liquibase formatted sql
--changeset mkh_alez:populate-roles-index

INSERT INTO roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;