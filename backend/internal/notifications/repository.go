package notifications

import (
	"context"
	"errors"
	"fmt"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

var ErrSettingsNotFound = errors.New("notification settings not found")

type Repository struct {
	db *pgxpool.Pool
}

func NewRepository(db *pgxpool.Pool) *Repository {
	return &Repository{db: db}
}

func (r *Repository) UpsertSettings(ctx context.Context, userID string, frequencyMinutes int) (Settings, error) {
	const query = `
		INSERT INTO user_notification_settings (user_id, frequency_minutes)
		VALUES ($1, $2)
		ON CONFLICT (user_id)
		DO UPDATE SET
			frequency_minutes = EXCLUDED.frequency_minutes,
			updated_at = NOW()
		RETURNING user_id, frequency_minutes, updated_at
	`

	var settings Settings
	err := r.db.QueryRow(ctx, query, userID, frequencyMinutes).
		Scan(&settings.UserID, &settings.FrequencyMinutes, &settings.UpdatedAt)
	if err != nil {
		return Settings{}, fmt.Errorf("upsert notification settings: %w", err)
	}

	return settings, nil
}

func (r *Repository) GetSettingsByUserID(ctx context.Context, userID string) (Settings, error) {
	const query = `
		SELECT user_id, frequency_minutes, updated_at
		FROM user_notification_settings
		WHERE user_id = $1
	`

	var settings Settings
	err := r.db.QueryRow(ctx, query, userID).
		Scan(&settings.UserID, &settings.FrequencyMinutes, &settings.UpdatedAt)
	if err == nil {
		return settings, nil
	}
	if errors.Is(err, pgx.ErrNoRows) {
		return Settings{}, ErrSettingsNotFound
	}

	return Settings{}, fmt.Errorf("get notification settings: %w", err)
}
