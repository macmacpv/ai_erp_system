-- Personnel Module: Ranks and Employees

CREATE TABLE IF NOT EXISTS ranks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    weight INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS rank_permissions (
    rank_id BIGINT REFERENCES ranks(id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (rank_id, permission_id)
);

CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    badge_number VARCHAR(20),
    rank_id BIGINT REFERENCES ranks(id) ON DELETE SET NULL
);