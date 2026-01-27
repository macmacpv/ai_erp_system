-- Core Schema: Identity & ACE Permissions

CREATE TABLE IF NOT EXISTS ranks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    weight INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rank_id INTEGER REFERENCES ranks(id),
    is_active BOOLEAN DEFAULT TRUE,
    is_root BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS permission_groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    group_type VARCHAR(50) -- e.g., 'DEPARTMENT', 'SPECIAL'
);

CREATE TABLE IF NOT EXISTS permissions (
    id SERIAL PRIMARY KEY,
    node_string VARCHAR(255) UNIQUE NOT NULL -- e.g., 'erp.hiring.create'
);

CREATE TABLE IF NOT EXISTS user_groups (
    user_id INTEGER REFERENCES users(id),
    group_id INTEGER REFERENCES permission_groups(id),
    PRIMARY KEY (user_id, group_id)
);

-- Insert initial Root data (placeholder)
INSERT INTO ranks (name, weight) VALUES ('Superadmin', 999);