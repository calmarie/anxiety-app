package thoughts

import "time"

type Thought struct {
	ID           string    `json:"id"`
	UserID       string    `json:"user_id"`
	AnxietyLevel int       `json:"anxiety_level"`
	AnxietyType  string    `json:"anxiety_type"`
	Description  string    `json:"description"`
	CreatedAt    time.Time `json:"created_at"`
}

type SyncEntryInput struct {
	AnxietyLevel int    `json:"anxiety_level"`
	AnxietyType  string `json:"anxiety_type"`
	Description  string `json:"description"`
}

type SyncRequest struct {
	Entries []SyncEntryInput `json:"entries"`
}

type SyncResponse struct {
	Thoughts []Thought `json:"thoughts"`
}

type StatisticsResponse struct {
	Week  PeriodStatistics `json:"week"`
	Month PeriodStatistics `json:"month"`
	Year  PeriodStatistics `json:"year"`
}

type PeriodStatistics struct {
	Period                 string             `json:"period"`
	From                   time.Time          `json:"from"`
	To                     time.Time          `json:"to"`
	EntriesCount           int                `json:"entries_count"`
	AverageAnxietyLevel    float64            `json:"average_anxiety_level"`
	DailyDynamics          []DailyStatistic   `json:"daily_dynamics"`
	AnxietyTypeFrequencies []AnxietyTypeCount `json:"anxiety_type_frequencies"`
	MostAnxiousTime        string             `json:"most_anxious_time"`
}

type DailyStatistic struct {
	Date                string  `json:"date"`
	AverageAnxietyLevel float64 `json:"average_anxiety_level"`
	EntriesCount        int     `json:"entries_count"`
}

type AnxietyTypeCount struct {
	AnxietyType string `json:"anxiety_type"`
	Count       int    `json:"count"`
}
