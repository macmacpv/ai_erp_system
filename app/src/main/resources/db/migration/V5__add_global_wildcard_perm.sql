-- Add global wildcard permission node
INSERT INTO permissions (node_string) VALUES ('*') ON CONFLICT DO NOTHING;