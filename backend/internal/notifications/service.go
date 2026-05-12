package notifications

import (
	"context"
	"errors"
	"fmt"
	"math/rand"
	"strings"
	"time"

	"anxiety-backend/internal/thoughts"
)

var ErrInvalidInput = errors.New("invalid input")

type thoughtReader interface {
	ListByUserIDSince(ctx context.Context, userID string, since time.Time) ([]thoughts.Thought, error)
}

type Service struct {
	repo     *Repository
	thoughts thoughtReader
}

func NewService(repo *Repository, thoughts thoughtReader) *Service {
	return &Service{
		repo:     repo,
		thoughts: thoughts,
	}
}

func (s *Service) UpdateSettings(ctx context.Context, userID string, request UpdateSettingsRequest) (Settings, error) {
	if strings.TrimSpace(userID) == "" {
		return Settings{}, fmt.Errorf("%w: user id is required", ErrInvalidInput)
	}
	if request.FrequencyMinutes <= 0 {
		return Settings{}, fmt.Errorf("%w: frequency_minutes must be greater than 0", ErrInvalidInput)
	}

	return s.repo.UpsertSettings(ctx, userID, request.FrequencyMinutes)
}

func (s *Service) GetSettings(ctx context.Context, userID string) (Settings, error) {
	if strings.TrimSpace(userID) == "" {
		return Settings{}, fmt.Errorf("%w: user id is required", ErrInvalidInput)
	}

	return s.repo.GetSettingsByUserID(ctx, userID)
}

func (s *Service) GetSupportMessage(ctx context.Context, userID string) (SupportMessageResponse, error) {
	if strings.TrimSpace(userID) == "" {
		return SupportMessageResponse{}, fmt.Errorf("%w: user id is required", ErrInvalidInput)
	}

	settings, err := s.repo.GetSettingsByUserID(ctx, userID)
	if err != nil {
		return SupportMessageResponse{}, err
	}

	now := time.Now().UTC()
	dayStart := time.Date(now.Year(), now.Month(), now.Day(), 0, 0, 0, 0, time.UTC)

	dailyThoughts, err := s.thoughts.ListByUserIDSince(ctx, userID, dayStart)
	if err != nil {
		return SupportMessageResponse{}, err
	}

	average := averageDailyAnxiety(dailyThoughts)
	category, messages := supportMessagesForLevel(average)
	message := messages[rand.Intn(len(messages))]

	return SupportMessageResponse{
		FrequencyMinutes:    settings.FrequencyMinutes,
		AverageDailyAnxiety: average,
		AnxietyRange:        category,
		Message:             message,
		GeneratedAt:         now,
	}, nil
}

func averageDailyAnxiety(thoughtsList []thoughts.Thought) float64 {
	if len(thoughtsList) == 0 {
		return 0
	}

	sum := 0
	for _, thought := range thoughtsList {
		sum += thought.AnxietyLevel
	}

	return float64(sum) / float64(len(thoughtsList))
}

func supportMessagesForLevel(level float64) (string, []string) {
	switch {
	case level <= 3:
		return "low", lowAnxietyMessages
	case level <= 6:
		return "medium", mediumAnxietyMessages
	default:
		return "high", highAnxietyMessages
	}
}

var lowAnxietyMessages = []string{
	"Не забывай иногда смотреть по сторонам, а не только в мысли.",
	"Сегодня можно никуда не спешить хотя бы пару минут.",
	"Иногда чашка чего-то тёплого уже делает день легче.",
	"Ты не обязан быть продуктивным всё время.",
	"Постарайся заметить что-то приятное сегодня.",
	"Даже маленький отдых имеет значение.",
	"Мир не требует от тебя идеальности.",
	"Можно просто спокойно прожить этот день.",
	"Иногда полезно немного замедлиться.",
	"Ты уже многое выдержал.",
	"Не забывай про воду и еду — это тоже забота о себе.",
	"Иногда лучший план — это сделать только необходимое.",
	"Всё постепенно становится на свои места.",
	"Сегодня уже достаточно того, что ты есть.",
	"Даже короткая прогулка может многое изменить.",
	"Ты можешь быть к себе мягче.",
	"Иногда стоит просто сделать паузу.",
	"Не всё требует немедленного решения.",
	"Позволь себе немного спокойствия.",
	"День не обязан быть идеальным, чтобы быть нормальным.",
	"Иногда полезно просто посидеть в тишине.",
	"Ты имеешь право уставать.",
	"Даже небольшие хорошие моменты важны.",
	"Постарайся сегодня немного выдохнуть.",
	"Не забывай отдыхать между делами.",
	"Мир подождёт пару минут.",
	"Можно делать всё в своём темпе.",
	"Сегодня тоже можно начать заново.",
	"Иногда достаточно просто продолжать идти.",
	"Ты не обязан всё держать под контролем.",
	"Позволь себе немного обычной жизни.",
	"Иногда музыка помогает больше мыслей.",
	"Даже спокойный день — это уже хорошо.",
	"Нормально иногда ничего не успевать.",
	"Попробуй сегодня лечь спать чуть раньше.",
	"Ты заслуживаешь доброты, даже от самого себя.",
	"Иногда полезно выйти ненадолго на воздух.",
	"Не забывай замечать хорошие мелочи.",
	"Мир становится тише, когда ты немного замедляешься.",
	"Ты уже делаешь достаточно.",
}

var mediumAnxietyMessages = []string{
	"Сейчас тебе может быть тяжело, но это состояние пройдёт.",
	"Тревога усиливает мысли, но не делает их правдой.",
	"Не нужно решать всё сразу.",
	"Попробуй сосредоточиться только на ближайшем шаге.",
	"Ты не один в этом состоянии.",
	"Даже сильные эмоции со временем становятся тише.",
	"Сейчас важно быть бережнее к себе.",
	"Тебе не нужно справляться идеально.",
	"Один спокойный вдох — уже начало.",
	"Постарайся вернуть внимание в настоящий момент.",
	"Мысли — это не всегда факты.",
	"Ты можешь пережить этот момент.",
	"Иногда телу нужен отдых, а не контроль.",
	"Попробуй немного замедлиться.",
	"Сейчас достаточно просто продолжать идти дальше.",
	"Ты уже выдержал много сложных моментов.",
	"Тревога не знает будущего.",
	"Постарайся не бороться с каждой мыслью.",
	"Один неприятный день не определяет всю жизнь.",
	"Ты не обязан быть сильным постоянно.",
	"Даже сейчас у тебя есть опора внутри.",
	"Постарайся переключиться на что-то физическое: дыхание, воду, прогулку.",
	"Не всё требует срочного решения.",
	"Тревога любит катастрофы, но реальность обычно спокойнее.",
	"Ты можешь дать себе немного времени.",
	"Сейчас главное — не давить на себя сильнее.",
	"Всё проходит шаг за шагом.",
	"Попробуй заметить пять вещей вокруг себя.",
	"Иногда организму просто нужен покой.",
	"Ты не сломан.",
	"Сейчас важно не идеальное состояние, а забота о себе.",
	"Не нужно побеждать тревогу за один день.",
	"Ты справляешься, даже если этого не чувствуешь.",
}

var highAnxietyMessages = []string{
	"Сейчас тревога может казаться огромной, но она не будет вечной.",
	"Тебе не нужно решать всю жизнь прямо сейчас.",
	"Сосредоточься только на ближайших нескольких минутах.",
	"Сделай медленный вдох. Потом ещё один.",
	"Сейчас ты в безопасности.",
	"Тревога создаёт ощущение опасности, даже когда её нет.",
	"Не пытайся контролировать всё сразу.",
	"Этот момент пройдёт.",
	"Попробуй почувствовать опору под ногами.",
	"Даже очень сильная тревога со временем ослабевает.",
	"Сейчас достаточно просто оставаться рядом с собой.",
	"Не верь каждой пугающей мысли.",
	"Ты не обязан сейчас быть продуктивным.",
	"Попробуй сделать что-то простое и знакомое.",
	"Один шаг за раз — этого достаточно.",
	"Ты уже переживал тяжёлые состояния раньше.",
	"Тело может быть напряжено, но это временно.",
	"Сейчас не нужно принимать важные решения.",
	"Тревога говорит громко, но это не значит, что она права.",
	"Постарайся вернуть внимание к дыханию.",
	"Даже если сейчас сложно, это состояние изменится.",
	"Ты не один.",
	"Мир вокруг не так опасен, как кажется тревоге.",
	"Сейчас важно быть к себе максимально мягким.",
	"Не нужно бороться с собой.",
	"Позволь себе пережить этот момент постепенно.",
	"Иногда лучший шаг — просто переждать волну тревоги.",
	"Всё не обязано решиться сегодня.",
	"Ты можешь попросить поддержки.",
	"Попробуй немного расслабить челюсть и плечи.",
	"Сейчас твоей нервной системе нужен покой.",
	"Ты не обязан справляться идеально.",
	"Даже сильная тревога не длится вечно.",
	"Сейчас главное — дышать и двигаться маленькими шагами.",
}
