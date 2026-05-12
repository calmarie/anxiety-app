package thoughts

import (
	"context"
	"errors"
	"fmt"
	"math"
	"sort"
	"strings"
	"time"
)

var ErrInvalidInput = errors.New("invalid input")

type Service struct {
	repo *Repository
}

func NewService(repo *Repository) *Service {
	return &Service{repo: repo}
}

func (s *Service) Sync(ctx context.Context, userID string, request SyncRequest) (SyncResponse, error) {
	if strings.TrimSpace(userID) == "" {
		return SyncResponse{}, fmt.Errorf("%w: user id is required", ErrInvalidInput)
	}

	for _, entry := range request.Entries {
		if err := validateEntry(entry); err != nil {
			return SyncResponse{}, err
		}
	}

	if err := s.repo.CreateMany(ctx, userID, request.Entries); err != nil {
		return SyncResponse{}, err
	}

	thoughts, err := s.repo.ListByUserID(ctx, userID)
	if err != nil {
		return SyncResponse{}, err
	}

	return SyncResponse{Thoughts: thoughts}, nil
}

func (s *Service) ListByUserID(ctx context.Context, userID string) (SyncResponse, error) {
	if strings.TrimSpace(userID) == "" {
		return SyncResponse{}, fmt.Errorf("%w: user id is required", ErrInvalidInput)
	}

	thoughts, err := s.repo.ListByUserID(ctx, userID)
	if err != nil {
		return SyncResponse{}, err
	}

	return SyncResponse{Thoughts: thoughts}, nil
}

func (s *Service) GetStatistics(ctx context.Context, userID string) (StatisticsResponse, error) {
	if strings.TrimSpace(userID) == "" {
		return StatisticsResponse{}, fmt.Errorf("%w: user id is required", ErrInvalidInput)
	}

	now := time.Now().UTC()

	weekThoughts, err := s.repo.ListByUserIDSince(ctx, userID, startOfDay(now).AddDate(0, 0, -6))
	if err != nil {
		return StatisticsResponse{}, err
	}

	monthThoughts, err := s.repo.ListByUserIDSince(ctx, userID, startOfDay(now).AddDate(0, 0, -29))
	if err != nil {
		return StatisticsResponse{}, err
	}

	yearThoughts, err := s.repo.ListByUserIDSince(ctx, userID, startOfDay(now).AddDate(0, 0, -364))
	if err != nil {
		return StatisticsResponse{}, err
	}

	return StatisticsResponse{
		Week:  buildPeriodStatistics("week", weekThoughts, 7, now),
		Month: buildPeriodStatistics("month", monthThoughts, 30, now),
		Year:  buildPeriodStatistics("year", yearThoughts, 365, now),
	}, nil
}

func validateEntry(entry SyncEntryInput) error {
	if entry.AnxietyLevel < 0 || entry.AnxietyLevel > 10 {
		return fmt.Errorf("%w: anxiety_level must be between 0 and 10", ErrInvalidInput)
	}
	if len(strings.TrimSpace(entry.AnxietyType)) == 0 {
		return fmt.Errorf("%w: anxiety_type is required", ErrInvalidInput)
	}
	if len(strings.TrimSpace(entry.Description)) == 0 {
		return fmt.Errorf("%w: description is required", ErrInvalidInput)
	}

	return nil
}

func buildPeriodStatistics(period string, thoughts []Thought, days int, now time.Time) PeriodStatistics {
	from := startOfDay(now).AddDate(0, 0, -(days - 1))
	to := now

	dailySums := make(map[string]int, days)
	dailyCounts := make(map[string]int, days)
	typeCounts := make(map[string]int)
	timeSums := map[string]int{
		"night":   0,
		"morning": 0,
		"day":     0,
		"evening": 0,
	}
	timeCounts := map[string]int{
		"night":   0,
		"morning": 0,
		"day":     0,
		"evening": 0,
	}

	totalLevel := 0
	for _, thought := range thoughts {
		totalLevel += thought.AnxietyLevel

		dayKey := thought.CreatedAt.UTC().Format("2006-01-02")
		dailySums[dayKey] += thought.AnxietyLevel
		dailyCounts[dayKey]++

		typeCounts[thought.AnxietyType]++

		bucket := timeBucket(thought.CreatedAt.UTC())
		timeSums[bucket] += thought.AnxietyLevel
		timeCounts[bucket]++
	}

	dailyKeys := make([]string, 0, len(dailyCounts))
	for key := range dailyCounts {
		dailyKeys = append(dailyKeys, key)
	}
	sort.Strings(dailyKeys)

	dailyDynamics := make([]DailyStatistic, 0, len(dailyKeys))
	for _, key := range dailyKeys {
		count := dailyCounts[key]
		dailyDynamics = append(dailyDynamics, DailyStatistic{
			Date:                key,
			AverageAnxietyLevel: round2(float64(dailySums[key]) / float64(count)),
			EntriesCount:        count,
		})
	}

	typeFrequencies := make([]AnxietyTypeCount, 0, len(typeCounts))
	for anxietyType, count := range typeCounts {
		typeFrequencies = append(typeFrequencies, AnxietyTypeCount{
			AnxietyType: anxietyType,
			Count:       count,
		})
	}
	sort.Slice(typeFrequencies, func(i, j int) bool {
		if typeFrequencies[i].Count == typeFrequencies[j].Count {
			return typeFrequencies[i].AnxietyType < typeFrequencies[j].AnxietyType
		}
		return typeFrequencies[i].Count > typeFrequencies[j].Count
	})

	return PeriodStatistics{
		Period:                 period,
		From:                   from,
		To:                     to,
		EntriesCount:           len(thoughts),
		AverageAnxietyLevel:    averageLevel(totalLevel, len(thoughts)),
		DailyDynamics:          dailyDynamics,
		AnxietyTypeFrequencies: typeFrequencies,
		MostAnxiousTime:        mostAnxiousTime(timeSums, timeCounts),
	}
}

func startOfDay(t time.Time) time.Time {
	utc := t.UTC()
	return time.Date(utc.Year(), utc.Month(), utc.Day(), 0, 0, 0, 0, time.UTC)
}

func averageLevel(sum int, count int) float64 {
	if count == 0 {
		return 0
	}

	return round2(float64(sum) / float64(count))
}

func round2(value float64) float64 {
	return math.Round(value*100) / 100
}

func timeBucket(t time.Time) string {
	hour := t.Hour()
	switch {
	case hour >= 6 && hour < 12:
		return "morning"
	case hour >= 12 && hour < 18:
		return "day"
	case hour >= 18:
		return "evening"
	default:
		return "night"
	}
}

func mostAnxiousTime(timeSums map[string]int, timeCounts map[string]int) string {
	order := []string{"night", "morning", "day", "evening"}
	bestBucket := "unknown"
	bestAverage := -1.0

	for _, bucket := range order {
		count := timeCounts[bucket]
		if count == 0 {
			continue
		}

		avg := float64(timeSums[bucket]) / float64(count)
		if avg > bestAverage {
			bestAverage = avg
			bestBucket = bucket
		}
	}

	return bestBucket
}
