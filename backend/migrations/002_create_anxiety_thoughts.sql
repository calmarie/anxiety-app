CREATE TABLE IF NOT EXISTS anxiety_thoughts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    anxiety_level INT NOT NULL,
    anxiety_type TEXT NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_anxiety_thoughts_user_id_created_at
    ON anxiety_thoughts (user_id, created_at);
