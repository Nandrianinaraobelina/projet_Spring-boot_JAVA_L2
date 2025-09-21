-- ===========================
-- SUPPRESSION DE LA BASE (RESET COMPLET)
-- ===========================
DROP DATABASE IF EXISTS musique_db;

-- ===========================
-- CREATION DE LA BASE
-- ===========================
CREATE DATABASE musique_db;
USE musique_db;

-- ===========================
-- CREATION DES TABLES
-- ===========================

-- Table UTILISATEURS
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('client','admin','vendeur') DEFAULT 'client' NOT NULL
);

-- Table CLIENTS (CRM léger)
CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    cin VARCHAR(50) NULL,
    adresse VARCHAR(255),
    telephone VARCHAR(50),
    mail VARCHAR(150) NOT NULL UNIQUE,
    purchase_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table MATERIELS DE MUSIQUE
CREATE TABLE equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price_sale DECIMAL(10,2) NOT NULL COMMENT 'Prix de vente en Ariary',
    rental_price DECIMAL(10,2) NULL COMMENT 'Prix de location par jour en Ariary',
    quantity_available INT NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    category VARCHAR(100),
    active TINYINT(1) NOT NULL DEFAULT 1,
    available TINYINT(1) NOT NULL DEFAULT 1
);

-- Table PANIER (articles non encore commandés)
CREATE TABLE cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
);

-- Table COMMANDES
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL COMMENT 'Montant total en Ariary',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    order_type VARCHAR(20) NOT NULL DEFAULT 'SALE',
    shipping_name VARCHAR(150),
    shipping_address VARCHAR(255),
    shipping_city VARCHAR(100),
    shipping_country VARCHAR(100),
    shipping_postal_code VARCHAR(30),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table ARTICLES DE COMMANDE
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL COMMENT 'Prix total ligne en Ariary',
    is_rental TINYINT(1) NOT NULL DEFAULT 0,
    rental_days INT NULL,
    rental_start_date DATE NULL,
    rental_end_date DATE NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
);

-- Table FACTURES
CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    invoice_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL COMMENT 'Montant en Ariary',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
