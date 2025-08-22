-- Add accepted_at column to jobs table
ALTER TABLE jobs 
ADD COLUMN IF NOT EXISTS accepted_at TIMESTAMPTZ;
