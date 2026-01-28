-- Add color support for UI badges
ALTER TABLE ranks ADD COLUMN color VARCHAR(7) DEFAULT '#808080';
ALTER TABLE permission_groups ADD COLUMN color VARCHAR(7) DEFAULT '#808080';