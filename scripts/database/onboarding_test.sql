/*
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    manager_id INT NOT NULL,
    job_title VARCHAR(100), -- update to position_id (add positions table if structure gets more complex)
    department VARCHAR(100), -- update to department_id (add departments table if structure gets more complex)
    employment_type ENUM('FULL_TIME', 'PART_TIME', 'INTERN') NOT NULL,
    employment_status ENUM('ACTIVE', 'TERMINATED', 'RESIGNED', 'RETIRED', 'ON_LEAVE', 'SUSPENDED') NOT NULL,
    hire_date DATE NOT NULL,
    start_date DATE DEFAULT NULL,
    termination_date DATE DEFAULT NULL,
    termination_reason VARCHAR(100), -- possible reasons could be 'Resignation', 'Dismissal', 'End of Contract' or 'Retirement'
    retention_end_date DATE DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    last_updated_by INT DEFAULT NULL,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manager_id) REFERENCES employees(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (last_updated_by) REFERENCES users(id)
);
*/

USE leanx_erp_prod;

INSERT INTO employees (id, first_name, last_name, email, manager_id, job_title, department, employment_type, employment_status, hire_date, start_date, created_by) 
VALUES (1, 'Max', 'Musterman', 's_gruettmueller22@stud.hwr-berlin.de', 1, 'Chief Executive Officer (CEO)', null, 'FULL_TIME', 'ACTIVE', '2025-03-01', '2025-03-28', 3); -- test employee


INSERT INTO employees (first_name, last_name, email, manager_id, job_title, department, employment_type, employment_status, hire_date, start_date, created_by) 
VALUES   
    ('Noah', 'Kania', 's_kania22@stud.hwr-berlin.de', 1, 'Senior Developer', 'IT', 'FULL_TIME', 'ACTIVE', '2025-03-01', '2025-03-28', 3), -- Noah
    ('Chris', 'Bonitzki', 's_bonitzki22@stud.hwr-berlin.de', 1, 'Senior Business Architect', 'Enterprise Systems', 'FULL_TIME', 'ACTIVE', '2025-03-01', '2025-03-28', 3), -- Chris
    ('Nathan', 'Brendler', 's_brendler22@stud.hwr-berlin.de', 1, 'Senior Business Architect', 'Enterprise Systems', 'FULL_TIME', 'ACTIVE', '2025-03-01', '2025-03-28', 3), -- Janis
    ('Janis', 'Grüttmüller', 's_gruettmueller22@stud.hwr-berlin.de', 1, 'Senior Developer', 'IT', 'FULL_TIME', 'ACTIVE', '2025-03-01', '2025-03-28', 3), -- Nathan
    ('Svea', 'von Popowski', 'sveavonpopowski@yahoo.de', 1, 'Ganz tolle Freundin', 'Hab dich lieb Abteilung', 'FULL_TIME', 'ACTIVE', '2025-03-01', '2025-03-28', 3); -- Svea
    
-- passwort: initERP@2025    
INSERT INTO users (name, status, type, password_hash, is_first_login, password_expiry_date, created_by) 
VALUES 
	('ADMIN_1', 'ACTIVE', 'ADMIN', '$2a$10$Z6v/1IM1G2x6e47i1HnhvuWAmNgTETU7RiYzc4kRxu7LdNy1.PARu', true, DATE_ADD(CURDATE(), INTERVAL 90 DAY), 3),
    ('ADMIN_2', 'ACTIVE', 'ADMIN', '$2a$10$Z6v/1IM1G2x6e47i1HnhvuWAmNgTETU7RiYzc4kRxu7LdNy1.PARu', true, DATE_ADD(CURDATE(), INTERVAL 90 DAY), 3);
    
INSERT INTO user_employee_link (user_id, employee_id)
VALUES
	(4, 2),
    (5, 5);