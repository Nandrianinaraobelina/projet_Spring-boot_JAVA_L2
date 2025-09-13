-- Création de la base
CREATE DATABASE IF NOT EXISTS musique_db;
USE musique_db;

-- Table UTILISATEURS
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('client','admin','vendeur') DEFAULT 'client' NOT NULL
);

-- Table MATERIELS DE MUSIQUE
DROP TABLE IF EXISTS equipment;
CREATE TABLE IF NOT EXISTS equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price_sale DECIMAL(10,2) NOT NULL COMMENT 'Prix de vente en Ariary',
    quantity_available INT NOT NULL DEFAULT 0,
    image_url VARCHAR(255)
);

-- Table PANIER (articles non encore commandés)
DROP TABLE IF EXISTS cart;
CREATE TABLE IF NOT EXISTS cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
);

-- Table COMMANDES
DROP TABLE IF EXISTS orders;
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL COMMENT 'Montant total en Ariary',
    status ENUM('en_attente','payee','annulee') DEFAULT 'en_attente',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Table ARTICLES DE COMMANDE
DROP TABLE IF EXISTS order_items;
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL COMMENT 'Prix unitaire en Ariary',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE CASCADE
);

-- Table FACTURES
DROP TABLE IF EXISTS invoices;
CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    invoice_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL COMMENT 'Montant en Ariary',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
