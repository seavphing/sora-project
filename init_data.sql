-- Create schemas for each microservice
CREATE SCHEMA auth_schema;
CREATE SCHEMA wallet_schema;
CREATE SCHEMA gift_code_schema;
CREATE SCHEMA transaction_schema;
CREATE SCHEMA exchange_rate_schema;

-- Users Table in auth_schema
CREATE TABLE auth_schema.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    default_currency VARCHAR(3) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for users table
CREATE INDEX idx_users_username ON auth_schema.users(username);
CREATE INDEX idx_users_email ON auth_schema.users(email);

-- Wallets Table in wallet_schema
CREATE TABLE wallet_schema.wallets (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES auth_schema.users(id),
    balance DECIMAL(19, 4) NOT NULL DEFAULT 0,
    currency_code VARCHAR(3) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on user_id for faster lookups
CREATE INDEX idx_wallets_user_id ON wallet_schema.wallets(user_id);

-- Gift Codes Table in gift_code_schema
CREATE TABLE gift_code_schema.gift_codes (
    code VARCHAR(16) PRIMARY KEY,
    amount DECIMAL(19, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    redeemed BOOLEAN NOT NULL DEFAULT FALSE,
    created_by_user_id BIGINT NOT NULL REFERENCES auth_schema.users(id),
    redeemed_by_user_id BIGINT REFERENCES auth_schema.users(id),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    redeemed_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create indexes on frequently queried fields for gift codes
CREATE INDEX idx_gift_codes_created_by ON gift_code_schema.gift_codes(created_by_user_id);
CREATE INDEX idx_gift_codes_redeemed_by ON gift_code_schema.gift_codes(redeemed_by_user_id);
CREATE INDEX idx_gift_codes_expires_at ON gift_code_schema.gift_codes(expires_at);
CREATE INDEX idx_gift_codes_redeemed ON gift_code_schema.gift_codes(redeemed);

-- Transactions Table in transaction_schema
CREATE TABLE transaction_schema.transactions (
    id VARCHAR(36) PRIMARY KEY,
    sender_id BIGINT REFERENCES auth_schema.users(id),
    receiver_id BIGINT REFERENCES auth_schema.users(id),
    sender_wallet_id UUID REFERENCES wallet_schema.wallets(id),
    receiver_wallet_id UUID REFERENCES wallet_schema.wallets(id),
    amount DECIMAL(19, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    type VARCHAR(30) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for transaction lookups
CREATE INDEX idx_transactions_sender_id ON transaction_schema.transactions(sender_id);
CREATE INDEX idx_transactions_receiver_id ON transaction_schema.transactions(receiver_id);
CREATE INDEX idx_transactions_sender_wallet_id ON transaction_schema.transactions(sender_wallet_id);
CREATE INDEX idx_transactions_receiver_wallet_id ON transaction_schema.transactions(receiver_wallet_id);
CREATE INDEX idx_transactions_created_at ON transaction_schema.transactions(created_at);
CREATE INDEX idx_transactions_type ON transaction_schema.transactions(type);

-- Exchange Rates Table in exchange_rate_schema
CREATE TABLE exchange_rate_schema.exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    source_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(19, 8) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(source_currency, target_currency)
);

-- Create index for quick lookup of currency pairs
CREATE INDEX idx_exchange_rates_currencies ON exchange_rate_schema.exchange_rates(source_currency, target_currency);

-- Initial Data: Default Exchange Rates
INSERT INTO exchange_rate_schema.exchange_rates (source_currency, target_currency, rate) VALUES
('USD', 'EUR', 0.92),
('USD', 'GBP', 0.79),
('USD', 'JPY', 151.50),
('USD', 'AUD', 1.52),
('USD', 'CAD', 1.37),
('USD', 'CHF', 0.90),
('USD', 'CNY', 7.20),
('USD', 'KHR', 4100.00),
('EUR', 'USD', 1.09),
('GBP', 'USD', 1.27),
('JPY', 'USD', 0.0066),
('AUD', 'USD', 0.66),
('CAD', 'USD', 0.73),
('CHF', 'USD', 1.11),
('CNY', 'USD', 0.14),
('KHR', 'USD', 0.00024);

-- Additional cross-rates for major currencies
INSERT INTO exchange_rate_schema.exchange_rates (source_currency, target_currency, rate) VALUES
('EUR', 'GBP', 0.86),
('EUR', 'JPY', 164.67),
('EUR', 'KHR', 4467.00),
('GBP', 'EUR', 1.16),
('GBP', 'JPY', 191.77),
('KHR', 'EUR', 0.00022);

-- Initial test users
-- Password for 'testuser' is 'password' (BCrypt hashed)
INSERT INTO auth_schema.users (username, password, email, first_name, last_name, default_currency)
VALUES ('testuser', '$2a$10$IfnKG.Cu9WGvjbv1MfbQr.dMYMfloaqcKdUqFf4MO2HhV/EV5sQF2', 'test@example.com', 'Test', 'User', 'USD');

-- Password for 'admin' is 'admin123' (BCrypt hashed)
INSERT INTO auth_schema.users (username, password, email, first_name, last_name, default_currency)
VALUES ('admin', '$2a$10$VBbfsaNJH84MEKZUu5xV5eA7MF5Bl2kXHkpJUxwRSQvSHJ2G5STEC', 'admin@example.com', 'Admin', 'User', 'USD');

-- Create a test wallet for testuser (UUID is generated statically for testing purposes)
INSERT INTO wallet_schema.wallets (id, user_id, balance, currency_code)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 1, 1000.00, 'USD');

-- Create a test gift code
INSERT INTO gift_code_schema.gift_codes (code, amount, currency_code, created_by_user_id, expires_at)
VALUES ('WELCOME50', 50.00, 'USD', 2, '2025-12-31 23:59:59');

-- Create a test transaction (system deposit to initial wallet)
INSERT INTO transaction_schema.transactions (id, receiver_id, receiver_wallet_id, amount, currency_code, status, type, description, created_at)
VALUES ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 1, '550e8400-e29b-41d4-a716-446655440000', 1000.00, 'USD', 'COMPLETED', 'SYSTEM_DEPOSIT', 'Initial wallet funding', '2023-01-01 12:00:00');