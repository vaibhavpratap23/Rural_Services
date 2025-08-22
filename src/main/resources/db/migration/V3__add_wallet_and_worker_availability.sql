-- Add wallet table
CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add is_available column to workers table
ALTER TABLE workers ADD COLUMN is_available BOOLEAN DEFAULT true;

-- Create index for wallet lookups
CREATE INDEX idx_wallets_user_id ON wallets(user_id);

-- Create index for available workers
CREATE INDEX idx_workers_available ON workers(is_available) WHERE is_available = true;
