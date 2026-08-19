-- Assumes GenericEntity's PK column is named "id" (uuid) and it also
-- manages created_at/updated_at as timestamp columns, matching the
-- pattern used by jwt_token_entity in the auth service. Adjust the id/
-- created_at/updated_at column definitions below if GenericEntity's real
-- mapping differs.

CREATE TABLE IF NOT EXISTS subject_entity (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL,
    title       VARCHAR(120) NOT NULL,
    color       VARCHAR(20) NOT NULL,
    icon        VARCHAR(60) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    deleted_at  TIMESTAMP NULL
);

CREATE INDEX IF NOT EXISTS idx_subject_user_id ON subject_entity (user_id);
CREATE INDEX IF NOT EXISTS idx_subject_user_id_deleted_at ON subject_entity (user_id, deleted_at);
CREATE INDEX IF NOT EXISTS idx_subject_user_id_title ON subject_entity (user_id, title);
