package thoughts

import (
	"context"
	"fmt"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type Repository struct {
	db *pgxpool.Pool
}

func NewRepository(db *pgxpool.Pool) *Repository {
	return &Repository{db: db}
}

func (r *Repository) CreateMany(ctx context.Context, userID string, entries []SyncEntryInput) error {
	if len(entries) == 0 {
		return nil
	}

	batch := &pgx.Batch{}
	for _, entry := range entries {
		batch.Queue(
			`INSERT INTO anxiety_thoughts (user_id, anxiety_level, anxiety_type, description)
			 VALUES ($1, $2, $3, $4)`,
			userID,
			entry.AnxietyLevel,
			entry.AnxietyType,
			entry.Description,
		)
	}

	results := r.db.SendBatch(ctx, batch)
	defer results.Close()

	for range entries {
		if _, err := results.Exec(); err != nil {
			return fmt.Errorf("insert thought batch: %w", err)
		}
	}

	return nil
}

func (r *Repository) ListByUserID(ctx context.Context, userID string) ([]Thought, error) {
	return r.listByUserIDAndSince(ctx, userID, nil)
}

func (r *Repository) ListByUserIDSince(ctx context.Context, userID string, since time.Time) ([]Thought, error) {
	return r.listByUserIDAndSince(ctx, userID, &since)
}

func (r *Repository) listByUserIDAndSince(ctx context.Context, userID string, since *time.Time) ([]Thought, error) {
	query := `
		SELECT id, user_id, anxiety_level, anxiety_type, description, created_at
		FROM anxiety_thoughts
		WHERE user_id = $1
	`
	args := []any{userID}
	if since != nil {
		query += ` AND created_at >= $2`
		args = append(args, *since)
	}
	query += ` ORDER BY created_at ASC, id ASC`

	rows, err := r.db.Query(ctx, query, args...)
	if err != nil {
		return nil, fmt.Errorf("query thoughts: %w", err)
	}
	defer rows.Close()

	thoughts := make([]Thought, 0)
	for rows.Next() {
		var thought Thought
		if err := rows.Scan(
			&thought.ID,
			&thought.UserID,
			&thought.AnxietyLevel,
			&thought.AnxietyType,
			&thought.Description,
			&thought.CreatedAt,
		); err != nil {
			return nil, fmt.Errorf("scan thought: %w", err)
		}

		thoughts = append(thoughts, thought)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterate thoughts: %w", err)
	}

	return thoughts, nil
}
