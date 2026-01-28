-- Move group assignment from Account to Employee

DROP TABLE IF EXISTS user_groups;

CREATE TABLE IF NOT EXISTS employee_groups (
    employee_id BIGINT REFERENCES employees(id) ON DELETE CASCADE,
    group_id BIGINT REFERENCES permission_groups(id) ON DELETE CASCADE,
    PRIMARY KEY (employee_id, group_id)
);