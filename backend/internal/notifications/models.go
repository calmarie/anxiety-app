package notifications

import "time"

type Settings struct {
	UserID           string    `json:"user_id"`
	FrequencyMinutes int       `json:"frequency_minutes"`
	UpdatedAt        time.Time `json:"updated_at"`
}

type UpdateSettingsRequest struct {
	FrequencyMinutes int `json:"frequency_minutes"`
}

type SupportMessageResponse struct {
	FrequencyMinutes    int       `json:"frequency_minutes"`
	AverageDailyAnxiety float64   `json:"average_daily_anxiety"`
	AnxietyRange        string    `json:"anxiety_range"`
	Message             string    `json:"message"`
	GeneratedAt         time.Time `json:"generated_at"`
}
