-- Add performance indexes for better query optimization

-- Jobs table indexes
CREATE INDEX IF NOT EXISTS idx_jobs_location ON jobs(location_lat, location_lng);
CREATE INDEX IF NOT EXISTS idx_jobs_budget ON jobs(budget);
CREATE INDEX IF NOT EXISTS idx_jobs_created_at ON jobs(created_at);
CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
CREATE INDEX IF NOT EXISTS idx_jobs_category ON jobs(category_id);
CREATE INDEX IF NOT EXISTS idx_jobs_client_id ON jobs(client_id);

-- Worker profiles indexes
CREATE INDEX IF NOT EXISTS idx_workers_location ON workers(location_lat, location_lng);
CREATE INDEX IF NOT EXISTS idx_workers_verification ON workers(verification_status);
CREATE INDEX IF NOT EXISTS idx_workers_radius ON workers(radius_km);

-- Client profiles indexes
CREATE INDEX IF NOT EXISTS idx_clients_location ON clients(location_lat, location_lng);

-- Ratings table indexes
CREATE INDEX IF NOT EXISTS idx_ratings_reviewee ON ratings(reviewee_id);
CREATE INDEX IF NOT EXISTS idx_ratings_job ON ratings(job_id);
CREATE INDEX IF NOT EXISTS idx_ratings_created_at ON ratings(created_at);

-- Documents table indexes
CREATE INDEX IF NOT EXISTS idx_documents_worker ON documents(worker_id);
CREATE INDEX IF NOT EXISTS idx_documents_verification ON documents(verification_status);

-- Users table indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Composite indexes for complex queries
CREATE INDEX IF NOT EXISTS idx_jobs_status_location ON jobs(status, location_lat, location_lng);
CREATE INDEX IF NOT EXISTS idx_jobs_category_budget ON jobs(category_id, budget);
CREATE INDEX IF NOT EXISTS idx_workers_verification_location ON workers(verification_status, location_lat, location_lng);
