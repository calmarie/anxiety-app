package users

import (
	"anxiety-backend/internal/auth"
	"context"
	"errors"
	"fmt"
	"regexp"
	"strings"

	"github.com/google/uuid"
	"golang.org/x/crypto/bcrypt"
)

var ErrInvalidCredentials = errors.New("invalid credentials")
var ErrInvalidInput = errors.New("invalid input")

var emailRegex = regexp.MustCompile(`^[^\s@]+@[^\s@]+\.[^\s@]+$`)

type AuthService struct {
	repo   *Repository
	tokens *auth.JWTManager
}

func NewAuthService(repo *Repository, tokens *auth.JWTManager) *AuthService {
	return &AuthService{
		repo:   repo,
		tokens: tokens,
	}
}

func (s *AuthService) Register(ctx context.Context, input RegisterInput) (AuthResponse, error) {
	if err := validateRegisterInput(input); err != nil {
		return AuthResponse{}, err
	}

	passwordHash, err := bcrypt.GenerateFromPassword([]byte(input.Password), bcrypt.DefaultCost)
	if err != nil {
		return AuthResponse{}, fmt.Errorf("hash password: %w", err)
	}

	user, err := s.repo.Create(ctx, input, string(passwordHash))
	if err != nil {
		return AuthResponse{}, err
	}

	return s.buildAuthResponse(user)
}

func (s *AuthService) Login(ctx context.Context, input LoginInput) (AuthResponse, error) {
	if err := validateLoginInput(input); err != nil {
		return AuthResponse{}, err
	}

	user, err := s.repo.GetByEmail(ctx, input.Email)
	if err != nil {
		if errors.Is(err, ErrUserNotFound) {
			return AuthResponse{}, ErrInvalidCredentials
		}
		return AuthResponse{}, err
	}

	if err := bcrypt.CompareHashAndPassword([]byte(user.PasswordHash), []byte(input.Password)); err != nil {
		return AuthResponse{}, ErrInvalidCredentials
	}

	return s.buildAuthResponse(user)
}

func (s *AuthService) GetByID(ctx context.Context, id string) (User, error) {
	if strings.TrimSpace(id) == "" {
		return User{}, ErrInvalidInput
	}

	return s.repo.GetByID(ctx, id)
}

func (s *AuthService) buildAuthResponse(user User) (AuthResponse, error) {
	userID, err := uuid.Parse(user.ID)
	if err != nil {
		return AuthResponse{}, fmt.Errorf("parse user id: %w", err)
	}

	token, expiresAt, err := s.tokens.Generate(userID)
	if err != nil {
		return AuthResponse{}, err
	}

	user.PasswordHash = ""

	return AuthResponse{
		Token:     token,
		ExpiresAt: expiresAt,
		User:      user,
	}, nil
}

func validateRegisterInput(input RegisterInput) error {
	if !emailRegex.MatchString(strings.TrimSpace(input.Email)) {
		return fmt.Errorf("%w: invalid email", ErrInvalidInput)
	}
	if len(strings.TrimSpace(input.Name)) < 2 {
		return fmt.Errorf("%w: name must contain at least 2 characters", ErrInvalidInput)
	}
	if len(input.Password) < 8 {
		return fmt.Errorf("%w: password must contain at least 8 characters", ErrInvalidInput)
	}

	return nil
}

func validateLoginInput(input LoginInput) error {
	if !emailRegex.MatchString(strings.TrimSpace(input.Email)) {
		return fmt.Errorf("%w: invalid email", ErrInvalidInput)
	}
	if strings.TrimSpace(input.Password) == "" {
		return fmt.Errorf("%w: password is required", ErrInvalidInput)
	}

	return nil
}
