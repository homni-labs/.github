-- liquibase formatted sql
-- changeset homni:001-initial-schema

CREATE TABLE feature_toggle (
    id           UUID           PRIMARY KEY,
    name         VARCHAR(255)   NOT NULL UNIQUE,
    description  VARCHAR(1000),
    enabled      BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP
);

CREATE INDEX idx_toggle_enabled ON feature_toggle (enabled);

CREATE TABLE environment (
    id         UUID         PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

INSERT INTO environment (id, name, created_at) VALUES (gen_random_uuid(), 'DEV', NOW());
INSERT INTO environment (id, name, created_at) VALUES (gen_random_uuid(), 'TEST', NOW());
INSERT INTO environment (id, name, created_at) VALUES (gen_random_uuid(), 'PROD', NOW());

CREATE TABLE toggle_environment (
    toggle_id      UUID NOT NULL REFERENCES feature_toggle(id) ON DELETE CASCADE,
    environment_id UUID NOT NULL REFERENCES environment(id) ON DELETE RESTRICT,
    PRIMARY KEY (toggle_id, environment_id)
);

CREATE INDEX idx_toggle_env_environment_id ON toggle_environment (environment_id);

CREATE TABLE api_key (
    id         UUID         PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    token_hash VARCHAR(64)  NOT NULL UNIQUE,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP
);

CREATE INDEX idx_api_key_hash_active ON api_key (token_hash) WHERE active = true;

CREATE TABLE app_user (
    id            UUID         PRIMARY KEY,
    oidc_subject  VARCHAR(255) UNIQUE,
    email         VARCHAR(255) NOT NULL,
    name          VARCHAR(255),
    role          VARCHAR(50)  NOT NULL DEFAULT 'READER',
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP
);

CREATE INDEX idx_app_user_oidc_subject ON app_user (oidc_subject);
CREATE UNIQUE INDEX idx_app_user_email ON app_user (email);

-- rollback DROP TABLE IF EXISTS toggle_environment; DROP TABLE IF EXISTS environment; DROP TABLE IF EXISTS app_user; DROP TABLE IF EXISTS api_key; DROP TABLE IF EXISTS feature_toggle;
