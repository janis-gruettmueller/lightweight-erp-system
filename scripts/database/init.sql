/* ************************************************************************************* *
 * File: init.sql                                                                        *
 * Version: 1.0                                                                          *
 * Author: Janis Grüttmüller on 17.02.2025                                               *
 * Description: sql script to initialize the LeanX ERP Systems                           *
 * productive database and configure key system users and access management              *
 *                                                                                       *
 * change history:                                                                       *
 * 17.02.2025 - initial schema, viwes, triggers and stored procedures                    *
 * (user administratin, logging and HR operations modul)                                 *
 * ************************************************************************************* */

/* ===================================================================================== *
 *                              create database and schema                               *
 * ===================================================================================== */

DROP DATABASE IF EXISTS leanx_erp_prod;

CREATE DATABASE leanx_erp_prod;

USE leanx_erp_prod;


/* ------------ Users, Roles and Permissions (User Administration Modul) ------------------ */
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    status ENUM('ACTIVE', 'LOCKED', 'DEACTIVATED') NOT NULL DEFAULT 'ACTIVE',
    /*
        - ACTIVE: User can log in and use the system.
        - LOCKED: User is temporarily blocked (e.g., failed logins, security issues).
        - DEACTIVATED: Soft delete -> User cannot log in, and role mappings are removed.
    */
    type ENUM('NORMAL', 'ADMIN', 'SYSTEM') NOT NULL DEFAULT 'NORMAL',
    password_hash VARCHAR(255) NOT NULL,
    password_expiry_date DATE NOT NULL,
    num_failed_login_attempts INT DEFAULT 0,
    last_login_at TIMESTAMP DEFAULT NULL,
    valid_until DATE DEFAULT NULL,
    created_by INT NOT NULL DEFAULT 1, -- Default to System user (user_id = 1)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_by INT DEFAULT NULL,
    last_updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (last_updated_by) REFERENCES users(id)
);

-- table for managing permissions per role
CREATE TABLE role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- table for managing user access rights
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id), 
    FOREIGN KEY (created_by) REFERENCES users(id)
);

/* --------------- System Administraion and password managemenr  ------------------------ */
CREATE TABLE password_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    password_hash VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE configurations  (
    id INT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(255) UNIQUE NOT NULL,
    config_value VARCHAR(255) NOT NULL,
    config_category ENUM(
        'PASSWORD_SETTINGS', 
        'EMAIL_SETTINGS', 
        'SECURITY_SETTINGS', 
        'SYSTEM_SETTINGS'
    ) NOT NULL,
    description TEXT,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_updated_by INT NOT NULL,
    FOREIGN KEY (last_updated_by) REFERENCES users(id)
);

/* ------------------------ Change Logging / Audit-Logs ------------------------------ */

CREATE TABLE security_audit_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    changed_by INT NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activity_type ENUM('ROLE_ASSIGNED', 'ROLE_REMOVED') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (changed_by) REFERENCES users(id)
);

CREATE TABLE user_history_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    changed_by INT NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    field_name ENUM(
        'user_status', 
        'is_verified', 
        'password_hash', 
        'num_failed_login_attempts', 
        'last_login_at', 
        'valid_until'
    ) DEFAULT NULL,
    old_value TEXT DEFAULT NULL,
    new_value TEXT NOT NULL,
    description TEXT DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (changed_by) REFERENCES users(id)
);

CREATE TABLE configuration_change_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    changed_by INT NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    config_key VARCHAR(255) NOT NULL,
    config_category VARCHAR(255) NOT NULL,
    old_value TEXT DEFAULT NULL,
    new_value TEXT NOT NULL,
    FOREIGN KEY (changed_by) REFERENCES users(id),
    FOREIGN KEY (config_key) REFERENCES configurations(config_key)
);

/*------------ Employee, Payroll, Salary and Benefits (HR Operations) -------------------*/

-- table to store essential employee information and personal data
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    job_titel VARCHAR(100),
    department VARCHAR(100),
    employment_type ENUM('FULL_TIME', 'PART_TIME', 'INTERN') NOT NULL,
    employment_status ENUM('ACTIVE', 'TERMINATED', 'RESIGNED', 'RETIRED', 'ON_LEAVE', 'SUSPENDED') NOT NULL,
    hire_date DATE NOT NULL,
    start_date DATE DEFAULT NULL,
    termination_date DATE DEFAULT NULL,
    termination_reason ENUM('Resignation', 'Dismissal', 'End of Contract', 'Retirement'),
    retention_end_date DATE DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INT NOT NULL,
    last_updated_by INT DEFAULT NULL,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (last_updated_by) REFERENCES users(id)
);

-- table to store historical payroll data
CREATE TABLE payroll (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    gross_salary DECIMAL(10,2) NOT NULL,
    total_deductions DECIMAL(10,2) NOT NULL,
    net_salary DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_status ENUM('PENDING', 'PROCESSED', 'FAILED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- table to keep track of employee salaries and adjustments over time
CREATE TABLE salaries (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    base_salary DECIMAL(10,2) NOT NULL,
    effective_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- table to store payroll deductions like taxes, benefits, etc.
CREATE TABLE payroll_deductions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    payroll_id INT NOT NULL,
    deduction_type ENUM('TAX', 'INSURANCE', 'PENSION', 'OTHER') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (payroll_id) REFERENCES payroll(id) ON DELETE CASCADE
);

-- table to store employees payment info
CREATE TABLE payment_details (
    employee_id INT PRIMARY KEY,
    payment_method ENUM('BANK_TRANSFER', 'CHECK') NOT NULL,
    account_holder_name VARCHAR(255) DEFAULT NULL,
    encrypted_bank_account VARBINARY(255) DEFAULT NULL,  -- Store encrypted bank account number (EU: IBAN)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

CREATE TABLE employee_benefits (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    benefit_type ENUM('HEALTH_INSURANCE', 'PENSION', 'PAID_LEAVE', 'OTHER') NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE DEFAULT NULL,
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);


-- table for the mapping of employees to user accounts
CREATE TABLE user_employee_link (
    user_id INT NOT NULL,
    employee_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, employee_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

/* ---------------- Transactions, General Ledger, Controlling (Finance Modul) -------------------- */


/* =============================================================================================== *
 *                                      define database Views                                      *
 * =============================================================================================== */

/* -------------------------------------- HR Operations Views ------------------------------------ */




/* -------------------------------------- FI Operations Views ------------------------------------ */




/* -------------------------------------- User Administration Views ------------------------------ */

CREATE VIEW user_role_view AS
SELECT
    u.id AS 'user id',
    u.name AS 'username',
    r.name AS 'role name'
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;


CREATE VIEW active_users_view AS
SELECT
    u.name AS 'username',
    e.first_name AS 'first name',
    e.last_name AS 'last_name',
    e.employment_status AS 'employment status',
    e.termination_date AS 'termination_date',
    u.status AS 'user status',
    u.type AS 'user type',
    u.last_login_at AS 'last login',
    u.created_by AS 'created by',
    u.created_at AS 'created at'
FROM users u
JOIN user_employee_link uel ON u.id = uel.user_id
JOIN employees e ON uel.employee_id = e.id
WHERE u.status = 'ACTIVE' AND u.type = 'NORMAL'
ORDER BY u.created_at;


CREATE VIEW deactived_users_view AS
SELECT
    u.name AS 'username',
    e.first_name AS 'first name',
    e.last_name AS 'last_name',
    e.employment_status AS 'employment status',
    e.termination_date AS 'termination_date',
    u.status AS 'user status',
    u.type AS 'user type',
    u.last_login_at AS 'last login',
    u.created_by AS 'created by',
    u.created_at AS 'created at'
FROM users u
LEFT JOIN user_employee_link uel ON u.id = uel.user_id
LEFT JOIN employees e ON uel.employee_id = e.id
WHERE u.status = 'DEACTIVATED' AND u.type = 'NORMAL'
ORDER BY u.created_at;



CREATE VIEW privileged_users_view AS
SELECT
    u.name AS 'username',
    e.first_name AS 'first name',
    e.last_name AS 'last_name',
    u.status AS 'user status',
    u.type AS 'user type',
    u.last_login_at AS 'last login',
    u.created_by AS 'created by',
    u.created_at AS 'created at'
FROM users u
LEFT JOIN user_employee_link uel ON u.id = uel.user_id
LEFT JOIN employees e ON uel.employee_id = e.id
WHERE u.type = 'ADMIN'
ORDER BY u.created_at;


CREATE VIEW system_users_view AS
SELECT
    name AS 'username',
    status AS 'user status',
    type AS 'user type',
    last_login_at AS 'last login',
    created_by AS 'created by',
    created_at AS 'created at'
FROM users
WHERE type = 'SYSTEM';


CREATE VIEW password_settings_view AS
SELECT * FROM configurations WHERE config_category = 'PASSWORD_SETTINGS'; 


/* =============================================================================================== *
 *                                    define database triggers                                     *
 * =============================================================================================== */


/* -------------------------------------- Trigger Syntax ----------------------------------------- *
 
 CREATE TRIGGER trigger_name
 {BEFORE | AFTER} {event}
 ON table_name
 FOR EACH ROW
 [WHEN condition]
 BEGIN
    -- Anweisungen, die bei Auslösung des Triggers ausgeführt werden
 END;

- trigger_name: Der Name des Triggers.
- {BEFORE | AFTER}: Gibt an, ob der Trigger vor oder nach dem Ereignis ausgelöst wird.
- {event}: Das Ereignis, das den Trigger auslöst (INSERT, UPDATE, DELETE)

* ---------------- triggers for logging changes in the user_history_log table --------------------- */

DELIMITER $$

CREATE TRIGGER log_new_user_creation
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO user_history_log (user_id, changed_by, changed_at, new_value, description)
    VALUES (NEW.id, NEW.created_by, NEW.created_at, 
			JSON_OBJECT('username', NEW.name, 'user_status', NEW.status, 'user_type', NEW.type, 'valid_until', NEW.valid_until), 
            'new user creation');
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER log_changes
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    -- log changes to the status field
    IF NEW.status != OLD.status THEN
        -- check if the account was locked
        IF NEW.status = 'LOCKED' THEN
            INSERT INTO history_log (user_id, changed_by, changed_at, field_name, old_value, new_value, description)
            VALUES (NEW.id, NEW.last_updated_by, NEW.last_updated_at, 'status', OLD.status, NEW.status, 'account locked');
        -- check if the account was deactivated
        ELSEIF NEW.status = 'DEACTIVATED' THEN
            INSERT INTO history_log (user_id, changed_by, changed_at, field_name, old_value, new_value, description)
            VALUES (NEW.id, NEW.last_updated_by, NEW.last_updated_at, 'status', OLD.status, NEW.status, 'account deactivated');
        -- check if the account was unlocked
        ELSEIF NEW.status = 'ACTIVE' AND OLD.status = 'LOCKED' THEN
            INSERT INTO history_log (user_id, changed_by, changed_at, field_name, old_value, new_value, description)
            VALUES (NEW.id, NEW.last_updated_by, NEW.last_updated_at, 'status', OLD.status, NEW.status, 'account unlocked');
        -- check if the account was reactivated
        ELSEIF NEW.status = 'ACTIVE' AND OLD.status = 'DELETED' THEN
            INSERT INTO history_log (user_id, changed_by, changed_at, field_name, old_value, new_value, description)
            VALUES (NEW.id, NEW.last_updated_by, NEW.last_updated_at, 'status', OLD.status, NEW.status, 'account reactivated');
        END IF;
    END IF;

    -- log failed login attempts
    IF NEW.num_failed_login_attempts != OLD.num_failed_login_attempts THEN
        INSERT INTO history_log (user_id, changed_by, changed_at, field_name, old_value, new_value, description)
        VALUES (NEW.id, NEW.last_updated_by, NEW.last_updated_at, 'num_failed_login_attempts', OLD.num_failed_login_attempts, NEW.num_failed_login_attempts, 'failed login attempt');
    END IF;

    -- log successful logins
    IF NEW.last_login_at != OLD.last_login_at THEN
        INSERT INTO history_log (user_id, changed_by, changed_at, field_name, old_value, new_value, description)
        VALUES (NEW.id, NEW.last_updated_by, NEW.last_updated_at, 'last_login_at', OLD.last_login_at, NEW.last_login_at, 'successful login');
    END IF;

    -- log password changes
    IF NEW.password_hash != OLD.password_hash THEN
        INSERT INTO history_log (user_id, changed_by, changed_at, field_name, old_value, new_value, description)
        VALUES (NEW.id, NEW.last_updated_by, NEW.last_updated_at, 'password_hash', OLD.password_hash, NEW.password_hash, 'password changed');
    END IF;

    -- log changes to valid until date
    IF NEW.valid_until != OLD.valid_until THEN 
        INSERT INTO history_log (user_id, changed_by, changed_at, field_name, old_value, new_value, description)
        VALUES (NEW.id, NEW.last_updated_by, NEW.last_updated_at, 'valid_until', OLD.valid_until, NEW.valid_until, 'valid until date changed');
    END IF;

    -- log changes to password_expiry_date
    IF NEW.password_expiry_date != OLD.password_expiry_date THEN 
        INSERT INTO history_log (user_id, changed_by, changed_at, field_name, old_value, new_value, description)
        VALUES (NEW.id, NEW.last_updated_by, NEW.last_updated_at, 'password_expiry_date', OLD.password_expiry_date, NEW.password_expiry_date, 'password expiry date changed');
    END IF;
END $$

DELIMITER ;



/* ------------------ triggers for logging changes in security_audit_log table ------------------- */

DELIMITER $$

CREATE TRIGGER log_access_provisioning
AFTER INSERT ON user_roles
FOR EACH ROW
BEGIN
    INSERT INTO security_audit_log (user_id, role_id, changed_by, changed_at, activity_type)
    VALUES (NEW.user_id, NEW.role_id, NEW.created_by, NEW.created_at, 'ROLE_ASSIGNED');
END $$

DELIMITER ;


DELIMITER $$

CREATE TRIGGER log_access_deprovisioning
AFTER DELETE ON user_roles
FOR EACH ROW
BEGIN
    INSERT INTO security_audit_log (user_id, role_id, changed_by, activity_type)
    VALUES (OLD.user_id, OLD.role_id, OLD.created_by, 'ROLE_REMOVED'); -- created_by is set to current user in business application logic prior to deleting the table entry
END $$

DELIMITER ;

DELIMITER $$

/* ------------- triggers for logging changes to password settings in configurations  table -------------- */

CREATE TRIGGER log_password_configuration_changes
AFTER UPDATE ON configurations 
FOR EACH ROW
BEGIN
    IF NEW.config_category = 'PASSWORD_SETTINGS' THEN
        -- Log changes to password setting key values
        IF NEW.config_value != OLD.config_value THEN
            INSERT INTO configuration_change_log (changed_by, changed_at, config_category, config_key, old_value, new_value)
            VALUES (NEW.last_updated_by, NEW.last_updated, OLD.config_category, OLD.config_key, OLD.config_value, NEW.config_value);
        END IF;
    END IF;
END $$

DELIMITER ;

/* ------------------------------ triggers for User Access Management ------------------------------ */

DELIMITER $$

CREATE TRIGGER set_password_expiry_date_on_update
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    DECLARE expiry_period INT;

    IF NEW.password_hash != OLD.password_hash THEN
        -- Get the password_expiry_period value from the configurations table and convert to INT
        SELECT CAST(value AS UNSIGNED) INTO expiry_period
        FROM configurations
        WHERE config_key = 'password.expiry_period';

        -- Set the password_expiry_date to the current date plus the interval from the configurations table
        SET NEW.password_expiry_date = DATE_ADD(CURDATE(), INTERVAL expiry_period DAY);
    END IF;
END $$

DELIMITER ;

/* ------------------------------- triggers for Password Management -------------------------------- */

DELIMITER $$

CREATE TRIGGER update_password_history
AFTER UPDATE ON password_history
FOR EACH ROW
BEGIN

END $$

DELIMITER ;

/* ------------------------------- triggers for the HR Modul tables -------------------------------- */

DELIMITER $$

CREATE TRIGGER set_retention_period
BEFORE UPDATE ON employees
FOR EACH ROW
BEGIN
    IF NEW.termination_date IS NOT NULL THEN
        SET NEW.retention_end_date = DATE_ADD(NEW.termination_date, INTERVAL 2 YEAR);
    END IF;
END $$

DELIMITER ;


/* =============================================================================================== *
 *                                  define stored procedures                                       *
 * =============================================================================================== */

/* ----------------------------- procedures for user management ---------------------------------- */

DELIMITER $$

CREATE PROCEDURE DeactivateUserAccount (
	IN user_id_param INT, 
    IN current_user_param INT
) BEGIN
    /*
    IF NOT EXISTS (SELECT 1 FROM users WHERE user_id = user_id_param) THEN 
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Input: no user exists with the provided user_id_param';
	END IF;
	*/
    START TRANSACTION;

    -- set user_status to 'DELETED'
    UPDATE users
    SET status = 'DEACTIVATED', last_updated_by = current_user_param
    WHERE id = user_id_param;

    -- set changed_by to current_user for logging purposes
    UPDATE user_roles
    SET created_by = current_user_param
    WHERE user_id = user_id_param;

    -- remove all roles associated to user account
    DELETE FROM user_roles WHERE user_id = user_id_param;

    -- remove all stored password hashes associated to user account
    DELETE FROM password_history WHERE user_id = user_id_param;

    /* 
    -- delete employee user mapping if exists
    IF EXISTS (SELECT 1 FROM user_employee_link WHERE user_id = user_id_param) THEN
		DELETE FROM user_employee_link
		WHERE user_id = user_id_param;
	END IF;
    */
    
    COMMIT;
END $$

DELIMITER ;

-- procedure for automated creation of normal user account 
DELIMITER $$

CREATE PROCEDURE CreateNewUserAccount (
	IN username_param VARCHAR(50), 
    IN password_hash_param VARCHAR(255),
    IN employee_id_param INT
) BEGIN
	DECLARE user_id INT;
    
    START TRANSACTION;
    
    INSERT INTO users (name, password_hash, password_expiry_date, created_by)
    VALUES (username_param, password_hash_param, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1);

    SET user_id = LAST_INSERT_ID();

    -- Assign default role
    INSERT INTO user_roles (user_id, role_id, created_by)
    VALUES (user_id, (SELECT id FROM roles WHERE name = 'Employee'), 2);
    
    -- Link employee to user account
    INSERT INTO user_employee_link (user_id, employee_id)
    VALUES (user_id, employee_id_param);
    
    COMMIT;
END $$

DELIMITER ;


/* ---------------------------- procedures for HR Modul -------------------------------- */
DELIMITER $$

CREATE PROCEDURE TerminateEmployee (
	employee_id_param INT
) BEGIN

    -- Declare variables for cursor
    DECLARE user_id INT;
    DECLARE done INT DEFAULT 0;
    DECLARE user_cursor CURSOR FOR 
        SELECT user_id FROM user_employee_link WHERE employee_id = employee_id_param;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

	START TRANSACTION;

    OPEN user_cursor;
    
    -- Loop through all associated users and deactivate each one
    read_loop: LOOP
        FETCH user_cursor INTO user_id;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Call DeactivateUserAccount procedure for each user
        CALL DeactivateUserAccount(user_id, 2); -- user_id = 2 is a system user
    END LOOP;
    
    CLOSE user_cursor;

        
	UPDATE employees
    SET employment_status = 
        CASE 
            WHEN termination_reason = 'Resignation' THEN 'RESIGNED'
            WHEN termination_reason = 'Retirement' THEN 'RETIRED'
            ELSE 'TERMINATED'
        END
    WHERE id = employee_id_param;
    
    COMMIT;
END $$

DELIMITER ;


/* =============================================================================================== *
 *                                  populate database with data                                    *
 * =============================================================================================== */


START TRANSACTION;
-- Create Roles
INSERT INTO roles (id, name, description) VALUES
    (1, 'System User', 'Used for automated processes and background jobs'),
    (2, 'Admin', 'Full access to system configurations s and user management'),
    (3, 'FI Ops', 'Manages financial transactions, reports, and analysis'),
    (4, 'HR Ops', 'Manages employee records, payroll, and benefits'),
    (5, 'Employee', 'Access to personal data, payroll records, and requests of absence')
;

-- Create Permissions
INSERT INTO permissions (id, name, description) VALUES
    (1, 'View Financial Data', 'Ability to view financial transactions and reports'),
    (2, 'Modify Financial Data', 'Ability to create and edit financial transactions'),
    (3, 'Approve Financial Data', 'Ability to approve or finalize financial data'),
    (4, 'View Payroll Data', 'Ability to view payroll and salary information'),
    (5, 'Modify Payroll Data', 'Ability to create and edit payroll and salary information'),
    (6, 'Approve Payroll Data', 'Ability to approve payroll and salary changes'),
    (7, 'View Employee Data', 'Ability to view employee personal information'),
    (8, 'Modify Employee Data', 'Ability to edit employee personal information'),
    (9, 'View Personal Data', 'Ability to view personal employee information and payroll data'),
    (10, 'Request for Change of Personal Data', 'Ability to submit a request to change personal information'),
    (11, 'Request of Absence', 'Ability to submit and view personal requests of absence'),
    (12, 'Create User', 'Ability to create new user accounts'),
    (13, 'Deactivate User', 'Ability to deactivate existing user accounts'),
    (14, 'Modify User Access', 'Ability to modify access rights of existing user accounts'),
    (15, 'Modify System Settings', 'Ability to modify system settings and configurations s'),
    (16, 'Run Background Jobs', 'Ability to start and run automated background jobs'),
    (17, 'Run Automated Processes', 'Ability to start and run automated system processes')
;

/* ------------------ create system users for initial system setup and configurations  --------------------------- */

-- system user used for initial system setup
INSERT INTO users (id, name, status, type, password_hash, password_expiry_date, created_by) 
VALUES (1, 'SYS_SETUP', 'LOCKED', 'SYSTEM', '', '1999-01-01', 1);

-- system user used for running automated processes and background jobs following system setup
INSERT INTO users (name, status, type, password_hash, password_expiry_date, created_by) 
VALUES ('SYS_', 'LOCKED', 'SYSTEM', '', '1999-01-01', 1);

/* ------------------------------------ Assign Permissions to Roles -------------------------------------------- */
-- System User
INSERT INTO role_permissions (role_id, permission_id, created_by) VALUES
    (1, 16, 1), (1, 17, 1) -- run automated processes and background jobs
;

-- Admin (Full Access)
INSERT INTO role_permissions (role_id, permission_id, created_by) VALUES
    (2, 12, 1), (2, 13, 1), (2, 14, 1),   -- User Access Management (Create, Deactivate, Modify Access)
    (2, 15, 1), (2, 16, 1)                -- Configure System Settings and start automated jobs
;

-- FI Ops (Finance Operations)
INSERT INTO role_permissions (role_id, permission_id, created_by) VALUES
    (3, 1, 1),  -- View Financial Data
    (3, 2, 1),  -- Modify Financial Data
    (3, 3, 1),  -- Approve Financial Data
    (3, 4, 1)   -- View Payroll Data
;

-- HR Ops (HR Operations)
INSERT INTO role_permissions (role_id, permission_id, created_by) VALUES
    (4, 4, 1), (4, 5, 1), (4, 6, 1),  -- Payroll Data (View, Modify, Approve)
    (4, 7, 1), (4, 8, 1)              -- Employee Data (View, Modify, Update)
;

-- Employee (Personal Access)
INSERT INTO role_permissions (role_id, permission_id,  created_by) VALUES
    (5, 9, 1),  -- View Payroll Data
    (5, 10, 1),  -- View Personal Employee & Payroll Data
    (5, 11, 1)   -- Submit or view Request of absence
;

-- Set Initial Password configurations 
INSERT INTO configurations (config_key, config_value, config_category, description, last_updated_by)
VALUES
    ('password.min_length', '8', 'PASSWORD_SETTINGS', 'Minimum password length', 1),
    ('password.max_length', '20', 'PASSWORD_SETTINGS', 'Maximum password length', 1),
    ('password.require_uppercase', 'true', 'PASSWORD_SETTINGS', 'Require uppercase letters', 1),
    ('password.require_lowercase', 'true', 'PASSWORD_SETTINGS', 'Require lowercase letters', 1),
    ('password.require_numbers', 'true', 'PASSWORD_SETTINGS', 'Require numeric characters', 1),
    ('password.require_special_characters', 'true', 'PASSWORD_SETTINGS', 'Require special characters', 1),
    ('password.history_size', '5', 'PASSWORD_SETTINGS', 'Number of previous passwords to check for reuse', 1),
    ('password.expiry_period', '90', 'PASSWORD_SETTINGS', 'Password expiry period in days', 1),
    ('password.num_failed_attempts_before_lockout', '5', 'PASSWORD_SETTINGS', 'Number of failed login attempts before locking the account', 1),
    ('password.lockout_duration', '30', 'PASSWORD_SETTINGS', 'Duration of account lockout in minutes', 1);

/* ---------------------------------- Create Default User for manual System Setup ----------------------------------------- */

-- password: "initERP@2025" hashed with BCrypt (it is highly recommended to lock the user following the initial setup!)
INSERT INTO users (name, status, type, password_hash, password_expiry_date, created_by) 
VALUES ('DEFAULT_USR', 'ACTIVE', 'ADMIN', '$2a$10$Z6v/1IM1G2x6e47i1HnhvuWAmNgTETU7RiYzc4kRxu7LdNy1.PARu', DATE_ADD(CURDATE(), INTERVAL 90 DAY), 1);

-- give SYS_ user the SYSTEM_USER role
INSERT INTO user_roles (user_id, role_id, created_by) 
VALUES (2, 1, 1);

-- give default user admin rights
INSERT INTO user_roles (user_id, role_id, created_by) 
VALUES (3, 2, 1);

COMMIT;