-- Add location fields to clients
ALTER TABLE clients 
ADD COLUMN IF NOT EXISTS location_lat DECIMAL(9,6),
ADD COLUMN IF NOT EXISTS location_lng DECIMAL(9,6);

-- Add new fields to workers (some fields may already exist)
ALTER TABLE workers 
ADD COLUMN IF NOT EXISTS aadhaar_number VARCHAR(12),
ADD COLUMN IF NOT EXISTS pan_number VARCHAR(10);

-- Update address column if it exists, or add it
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='workers' AND column_name='address') THEN
        ALTER TABLE workers ADD COLUMN address TEXT;
    END IF;
END $$;

-- Create verification_status enum if it doesn't exist, or add new values
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'verification_status') THEN
        CREATE TYPE verification_status AS ENUM ('PENDING', 'PENDING_BASIC', 'PENDING_FULL', 'VERIFIED', 'REJECTED');
    ELSE
        BEGIN
            ALTER TYPE verification_status ADD VALUE IF NOT EXISTS 'PENDING_BASIC';
        EXCEPTION WHEN duplicate_object THEN NULL;
        END;
        BEGIN
            ALTER TYPE verification_status ADD VALUE IF NOT EXISTS 'PENDING_FULL';
        EXCEPTION WHEN duplicate_object THEN NULL;
        END;
    END IF;
END $$;

-- Create document_type enum if it doesn't exist, or add new values
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'document_type') THEN
        CREATE TYPE document_type AS ENUM ('AADHAAR_CARD', 'PAN_CARD', 'SELFIE_WITH_AADHAAR', 'DRIVING_LICENSE');
    ELSE
        BEGIN
            ALTER TYPE document_type ADD VALUE IF NOT EXISTS 'AADHAAR_CARD';
        EXCEPTION WHEN duplicate_object THEN NULL;
        END;
        BEGIN
            ALTER TYPE document_type ADD VALUE IF NOT EXISTS 'PAN_CARD';
        EXCEPTION WHEN duplicate_object THEN NULL;
        END;
        BEGIN
            ALTER TYPE document_type ADD VALUE IF NOT EXISTS 'SELFIE_WITH_AADHAAR';
        EXCEPTION WHEN duplicate_object THEN NULL;
        END;
        BEGIN
            ALTER TYPE document_type ADD VALUE IF NOT EXISTS 'DRIVING_LICENSE';
        EXCEPTION WHEN duplicate_object THEN NULL;
        END;
    END IF;
END $$;

-- Create uploads directory structure (this will be handled by the application)
-- The application will create: uploads/documents/ directory structure

-- Create documents table if it doesn't exist
CREATE TABLE IF NOT EXISTS documents (
    id BIGSERIAL PRIMARY KEY,
    worker_id BIGINT NOT NULL REFERENCES workers(id) ON DELETE CASCADE,
    type document_type NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    verification_status verification_status DEFAULT 'PENDING',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_clients_location ON clients(location_lat, location_lng);
CREATE INDEX IF NOT EXISTS idx_workers_location ON workers(location_lat, location_lng);
CREATE INDEX IF NOT EXISTS idx_workers_verification ON workers(verification_status);
CREATE INDEX IF NOT EXISTS idx_documents_worker ON documents(worker_id);
