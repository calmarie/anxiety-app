package http

import (
	"context"
	"encoding/json"
	"errors"
	"net/http"
	"strings"
	"time"

	"anxiety-backend/internal/auth"
	"anxiety-backend/internal/notifications"
	"anxiety-backend/internal/thoughts"
	"anxiety-backend/internal/users"
)

type authService interface {
	Register(ctx context.Context, input users.RegisterInput) (users.AuthResponse, error)
	Login(ctx context.Context, input users.LoginInput) (users.AuthResponse, error)
	GetByID(ctx context.Context, id string) (users.User, error)
}

type thoughtService interface {
	Sync(ctx context.Context, userID string, request thoughts.SyncRequest) (thoughts.SyncResponse, error)
	ListByUserID(ctx context.Context, userID string) (thoughts.SyncResponse, error)
	GetStatistics(ctx context.Context, userID string) (thoughts.StatisticsResponse, error)
}

type notificationService interface {
	UpdateSettings(ctx context.Context, userID string, request notifications.UpdateSettingsRequest) (notifications.Settings, error)
	GetSettings(ctx context.Context, userID string) (notifications.Settings, error)
	GetSupportMessage(ctx context.Context, userID string) (notifications.SupportMessageResponse, error)
}

type Server struct {
	authService   authService
	thoughts      thoughtService
	notifications notificationService
	jwtManager    *auth.JWTManager
}

func NewServer(authService authService, thoughtService thoughtService, notificationService notificationService, jwtManager *auth.JWTManager) http.Handler {
	server := &Server{
		authService:   authService,
		thoughts:      thoughtService,
		notifications: notificationService,
		jwtManager:    jwtManager,
	}

	mux := http.NewServeMux()
	mux.HandleFunc("GET /health", server.handleHealth)
	mux.HandleFunc("POST /api/v1/auth/register", server.handleRegister)
	mux.HandleFunc("POST /api/v1/auth/login", server.handleLogin)
	mux.HandleFunc("GET /api/v1/auth/me", server.handleMe)
	mux.HandleFunc("POST /api/v1/thoughts/sync", server.handleThoughtSync)
	mux.HandleFunc("GET /api/v1/thoughts", server.handleThoughtList)
	mux.HandleFunc("GET /api/v1/thoughts/statistics", server.handleThoughtStatistics)
	mux.HandleFunc("POST /api/v1/notifications/settings", server.handleNotificationSettingsUpdate)
	mux.HandleFunc("GET /api/v1/notifications/settings", server.handleNotificationSettingsGet)
	mux.HandleFunc("GET /api/v1/notifications/support-message", server.handleSupportMessage)

	return withJSONContentType(withCORS(mux))
}

func (s *Server) handleHealth(w http.ResponseWriter, _ *http.Request) {
	writeJSON(w, http.StatusOK, map[string]string{"status": "ok"})
}

func (s *Server) handleRegister(w http.ResponseWriter, r *http.Request) {
	var input users.RegisterInput
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		writeError(w, http.StatusBadRequest, "invalid json body")
		return
	}

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()

	response, err := s.authService.Register(ctx, input)
	if err != nil {
		s.writeAuthError(w, err)
		return
	}

	writeJSON(w, http.StatusCreated, response)
}

func (s *Server) handleLogin(w http.ResponseWriter, r *http.Request) {
	var input users.LoginInput
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		writeError(w, http.StatusBadRequest, "invalid json body")
		return
	}

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()

	response, err := s.authService.Login(ctx, input)
	if err != nil {
		s.writeAuthError(w, err)
		return
	}

	writeJSON(w, http.StatusOK, response)
}

func (s *Server) handleMe(w http.ResponseWriter, r *http.Request) {
	claims, err := s.authorize(r)
	if err != nil {
		writeError(w, http.StatusUnauthorized, err.Error())
		return
	}

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()

	user, err := s.authService.GetByID(ctx, claims.UserID)
	if err != nil {
		if errors.Is(err, users.ErrUserNotFound) {
			writeError(w, http.StatusUnauthorized, "user not found")
			return
		}

		writeError(w, http.StatusInternalServerError, "failed to load user")
		return
	}

	user.PasswordHash = ""
	writeJSON(w, http.StatusOK, user)
}

func (s *Server) handleThoughtSync(w http.ResponseWriter, r *http.Request) {
	claims, err := s.authorize(r)
	if err != nil {
		writeError(w, http.StatusUnauthorized, err.Error())
		return
	}

	var request thoughts.SyncRequest
	if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
		writeError(w, http.StatusBadRequest, "invalid json body")
		return
	}

	ctx, cancel := context.WithTimeout(r.Context(), 10*time.Second)
	defer cancel()

	response, err := s.thoughts.Sync(ctx, claims.UserID, request)
	if err != nil {
		s.writeThoughtError(w, err)
		return
	}

	writeJSON(w, http.StatusOK, response)
}

func (s *Server) handleThoughtList(w http.ResponseWriter, r *http.Request) {
	claims, err := s.authorize(r)
	if err != nil {
		writeError(w, http.StatusUnauthorized, err.Error())
		return
	}

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()

	response, err := s.thoughts.ListByUserID(ctx, claims.UserID)
	if err != nil {
		s.writeThoughtError(w, err)
		return
	}

	writeJSON(w, http.StatusOK, response)
}

func (s *Server) handleThoughtStatistics(w http.ResponseWriter, r *http.Request) {
	claims, err := s.authorize(r)
	if err != nil {
		writeError(w, http.StatusUnauthorized, err.Error())
		return
	}

	ctx, cancel := context.WithTimeout(r.Context(), 10*time.Second)
	defer cancel()

	response, err := s.thoughts.GetStatistics(ctx, claims.UserID)
	if err != nil {
		s.writeThoughtError(w, err)
		return
	}

	writeJSON(w, http.StatusOK, response)
}

func (s *Server) handleNotificationSettingsUpdate(w http.ResponseWriter, r *http.Request) {
	claims, err := s.authorize(r)
	if err != nil {
		writeError(w, http.StatusUnauthorized, err.Error())
		return
	}

	var request notifications.UpdateSettingsRequest
	if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
		writeError(w, http.StatusBadRequest, "invalid json body")
		return
	}

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()

	response, err := s.notifications.UpdateSettings(ctx, claims.UserID, request)
	if err != nil {
		s.writeNotificationError(w, err)
		return
	}

	writeJSON(w, http.StatusOK, response)
}

func (s *Server) handleNotificationSettingsGet(w http.ResponseWriter, r *http.Request) {
	claims, err := s.authorize(r)
	if err != nil {
		writeError(w, http.StatusUnauthorized, err.Error())
		return
	}

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()

	response, err := s.notifications.GetSettings(ctx, claims.UserID)
	if err != nil {
		s.writeNotificationError(w, err)
		return
	}

	writeJSON(w, http.StatusOK, response)
}

func (s *Server) handleSupportMessage(w http.ResponseWriter, r *http.Request) {
	claims, err := s.authorize(r)
	if err != nil {
		writeError(w, http.StatusUnauthorized, err.Error())
		return
	}

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()

	response, err := s.notifications.GetSupportMessage(ctx, claims.UserID)
	if err != nil {
		s.writeNotificationError(w, err)
		return
	}

	writeJSON(w, http.StatusOK, response)
}

func (s *Server) writeAuthError(w http.ResponseWriter, err error) {
	switch {
	case errors.Is(err, users.ErrInvalidInput):
		writeError(w, http.StatusBadRequest, err.Error())
	case errors.Is(err, users.ErrUserAlreadyExists):
		writeError(w, http.StatusConflict, "user with this email already exists")
	case errors.Is(err, users.ErrInvalidCredentials):
		writeError(w, http.StatusUnauthorized, "invalid email or password")
	default:
		writeError(w, http.StatusInternalServerError, "internal server error")
	}
}

func (s *Server) writeThoughtError(w http.ResponseWriter, err error) {
	switch {
	case errors.Is(err, thoughts.ErrInvalidInput):
		writeError(w, http.StatusBadRequest, err.Error())
	default:
		writeError(w, http.StatusInternalServerError, "internal server error")
	}
}

func (s *Server) writeNotificationError(w http.ResponseWriter, err error) {
	switch {
	case errors.Is(err, notifications.ErrInvalidInput):
		writeError(w, http.StatusBadRequest, err.Error())
	case errors.Is(err, notifications.ErrSettingsNotFound):
		writeError(w, http.StatusNotFound, "notification settings not found")
	default:
		writeError(w, http.StatusInternalServerError, "internal server error")
	}
}

func (s *Server) authorize(r *http.Request) (*auth.Claims, error) {
	tokenString, ok := bearerToken(r.Header.Get("Authorization"))
	if !ok {
		return nil, errors.New("missing bearer token")
	}

	claims, err := s.jwtManager.Parse(tokenString)
	if err != nil {
		return nil, errors.New("invalid token")
	}

	return claims, nil
}

func withJSONContentType(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		next.ServeHTTP(w, r)
	})
}

func withCORS(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")
		w.Header().Set("Access-Control-Allow-Methods", "GET, POST, OPTIONS")

		if r.Method == http.MethodOptions {
			w.WriteHeader(http.StatusNoContent)
			return
		}

		next.ServeHTTP(w, r)
	})
}

func bearerToken(header string) (string, bool) {
	const prefix = "Bearer "
	if !strings.HasPrefix(header, prefix) {
		return "", false
	}

	token := strings.TrimSpace(strings.TrimPrefix(header, prefix))
	if token == "" {
		return "", false
	}

	return token, true
}

func writeJSON(w http.ResponseWriter, status int, payload any) {
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(payload)
}

func writeError(w http.ResponseWriter, status int, message string) {
	writeJSON(w, status, map[string]string{"error": message})
}
