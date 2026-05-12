package users

import (
	"context"
	"errors"
	"fmt"
	"strings"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"
)

var ErrUserAlreadyExists = errors.New("user already exists")
var ErrUserNotFound = errors.New("user not found")

type Repository struct {
	db *pgxpool.Pool
}

func NewRepository(db *pgxpool.Pool) *Repository {
	return &Repository{db: db}
}

func (r *Repository) Create(ctx context.Context, input RegisterInput, passwordHash string) (User, error) {
	const query = `
		INSERT INTO users (email, name, password_hash)
		VALUES ($1, $2, $3)
		RETURNING id, email, name, password_hash, created_at
	`

	var user User
	err := r.db.QueryRow(ctx, query, normalizeEmail(input.Email), strings.TrimSpace(input.Name), passwordHash).
		Scan(&user.ID, &user.Email, &user.Name, &user.PasswordHash, &user.CreatedAt)
	if err == nil {
		return user, nil
	}

	var pgErr *pgconn.PgError
	if errors.As(err, &pgErr) && pgErr.Code == "23505" {
		return User{}, ErrUserAlreadyExists
	}

	return User{}, fmt.Errorf("create user: %w", err)
}

func (r *Repository) GetByEmail(ctx context.Context, email string) (User, error) {
	const query = `
		SELECT id, email, name, password_hash, created_at
		FROM users
		WHERE email = $1
	`

	var user User
	err := r.db.QueryRow(ctx, query, normalizeEmail(email)).
		Scan(&user.ID, &user.Email, &user.Name, &user.PasswordHash, &user.CreatedAt)
	if err == nil {
		return user, nil
	}
	if errors.Is(err, pgx.ErrNoRows) {
		return User{}, ErrUserNotFound
	}

	return User{}, fmt.Errorf("get user by email: %w", err)
}

func (r *Repository) GetByID(ctx context.Context, id string) (User, error) {
	const query = `
		SELECT id, email, name, password_hash, created_at
		FROM users
		WHERE id = $1
	`

	var user User
	err := r.db.QueryRow(ctx, query, id).
		Scan(&user.ID, &user.Email, &user.Name, &user.PasswordHash, &user.CreatedAt)
	if err == nil {
		return user, nil
	}
	if errors.Is(err, pgx.ErrNoRows) {
		return User{}, ErrUserNotFound
	}

	return User{}, fmt.Errorf("get user by id: %w", err)
}

func normalizeEmail(email string) string {
	return strings.ToLower(strings.TrimSpace(email))
}
