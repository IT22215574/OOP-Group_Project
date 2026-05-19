-- PrimeEstate Database Schema
-- Database: OOP_Real_state
-- 4-Module Architecture: Agents | Users | Appointments | Advertisements

CREATE DATABASE IF NOT EXISTS OOP_Real_state CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE OOP_Real_state;

-- ============================================================
-- MODULE 1: Real Estate Agent Management (Member 1)
-- ============================================================
CREATE TABLE IF NOT EXISTS agents (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    full_name               VARCHAR(100) NOT NULL,
    contact_number          VARCHAR(20),
    email                   VARCHAR(150) NOT NULL UNIQUE,
    location                VARCHAR(100),
    experience_years        INT DEFAULT 0,
    property_specialization VARCHAR(100),
    availability_status     ENUM('available', 'busy', 'inactive') DEFAULT 'available',
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- MODULE 2: User Management (Member 2)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    full_name           VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL UNIQUE,
    password            VARCHAR(255) NOT NULL,
    phone               VARCHAR(20),
    preferred_location  VARCHAR(100),
    account_status      ENUM('active', 'inactive', 'suspended') DEFAULT 'active',
    role                ENUM('admin', 'agent', 'buyer') DEFAULT 'buyer',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- MODULE 3: Appointment Management (Member 3)
-- ============================================================
CREATE TABLE IF NOT EXISTS appointments (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    user_id             INT NOT NULL,
    agent_id            INT NOT NULL,
    appointment_date    DATE NOT NULL,
    appointment_time    TIME NOT NULL,
    property_type       VARCHAR(50),
    appointment_status  ENUM('pending', 'confirmed', 'rescheduled', 'cancelled', 'rejected') DEFAULT 'pending',
    agent_message       TEXT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
);

-- ============================================================
-- MODULE 4: Property Advertisement Management (Member 4)
-- ============================================================
CREATE TABLE IF NOT EXISTS advertisements (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    agent_id            INT NOT NULL,
    property_title      VARCHAR(200) NOT NULL,
    property_type       VARCHAR(50),
    price               DECIMAL(15, 2),
    location            VARCHAR(100),
    description         TEXT,
    availability_status ENUM('available', 'sold', 'rented') DEFAULT 'available',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
);

-- Advertisement images (up to 3 per advertisement)
CREATE TABLE IF NOT EXISTS advertisement_images (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    advertisement_id INT NOT NULL,
    image_path       VARCHAR(500) NOT NULL,
    image_order      INT DEFAULT 1,
    FOREIGN KEY (advertisement_id) REFERENCES advertisements(id) ON DELETE CASCADE
);

-- ============================================================
-- Existing: Properties table (kept for property browsing)
-- ============================================================
CREATE TABLE IF NOT EXISTS properties (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    price       DECIMAL(15, 2) NOT NULL,
    type        ENUM('sale', 'rent') NOT NULL DEFAULT 'sale',
    category    ENUM('house', 'apartment', 'villa', 'land', 'commercial') DEFAULT 'house',
    status      ENUM('available', 'sold', 'rented') DEFAULT 'available',
    bedrooms    INT DEFAULT 0,
    bathrooms   INT DEFAULT 0,
    area_sqft   DECIMAL(10, 2) DEFAULT 0,
    address     VARCHAR(255) NOT NULL,
    city        VARCHAR(100) NOT NULL,
    state       VARCHAR(100),
    zip_code    VARCHAR(20),
    agent_id    INT,
    image_url   VARCHAR(500),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS property_images (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    property_id INT NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    is_primary  BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

-- ============================================================
-- Sample Data
-- ============================================================

INSERT INTO agents (full_name, contact_number, email, location, experience_years, property_specialization, availability_status) VALUES
('John Silva',    '0777654321', 'john.silva@primeestate.com',   'Colombo',       8, 'Residential', 'available'),
('Sarah Perera',  '0779876543', 'sarah.perera@primeestate.com', 'Kandy',         5, 'Commercial',  'available'),
('Ruwan Fernando','0771122334', 'ruwan@primeestate.com',        'Gampaha',       3, 'Land',        'busy');

INSERT INTO users (full_name, email, password, phone, preferred_location, account_status, role) VALUES
('Admin User',   'admin@primeestate.com', '$2a$12$examplehashedpassword1', '0771234567', 'Colombo',  'active', 'admin'),
('Kamal Perera', 'kamal@example.com',    '$2a$12$examplehashedpassword2', '0712345678', 'Colombo',  'active', 'buyer'),
('Nimal Silva',  'nimal@example.com',    '$2a$12$examplehashedpassword3', '0723456789', 'Kandy',    'active', 'buyer');

INSERT INTO appointments (user_id, agent_id, appointment_date, appointment_time, property_type, appointment_status) VALUES
(2, 1, '2026-05-20', '10:00:00', 'Residential', 'pending'),
(3, 2, '2026-05-22', '14:30:00', 'Commercial',  'confirmed');

INSERT INTO advertisements (agent_id, property_title, property_type, price, location, description, availability_status) VALUES
(1, 'Modern Family Home in Colombo 7',  'Residential', 45000000, 'Colombo 7',   'Spacious modern home with open plan living.', 'available'),
(2, 'Prime Office Space - Kandy City',  'Commercial',  12000000, 'Kandy',       'Premium office space in the heart of Kandy.', 'available'),
(1, 'Luxury Villa - Negombo Beach',     'Residential', 75000000, 'Negombo',     'Private beachfront villa with 5 bedrooms.',   'available');

-- ============================================================
-- Migration: Run these on existing databases (already applied in CREATE TABLE above)
-- ============================================================
-- ALTER TABLE appointments
--     ADD COLUMN IF NOT EXISTS agent_message TEXT NULL AFTER appointment_status,
--     MODIFY COLUMN appointment_status ENUM('pending','confirmed','rescheduled','cancelled','rejected') DEFAULT 'pending';
