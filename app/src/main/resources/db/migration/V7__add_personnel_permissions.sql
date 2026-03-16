-- Personnel permissions for ACE system

INSERT INTO permissions (node_string) VALUES 
('erp.personnel.read'),
('erp.personnel.create'),
('erp.personnel.edit'),
('erp.personnel.delete')
ON CONFLICT DO NOTHING;