package config

import (
	"log"
	"os"
	"strconv"
	"time"
)

type Config struct {
	Port        string
	DatabaseURL string
	JWTSecret   string
	JWTIssuer   string
	JWTTokenTTL time.Duration
}

func MustLoad() Config {
	cfg := Config{
		Port:        getEnv("APP_PORT", "8080"),
		DatabaseURL: mustEnv("DATABASE_URL"),
		JWTSecret:   mustEnv("JWT_SECRET"),
		JWTIssuer:   getEnv("JWT_ISSUER", "anxiety-app"),
		JWTTokenTTL: mustDurationMinutes("JWT_TOKEN_TTL_MINUTES", 60*24*7),
	}

	return cfg
}

func mustEnv(key string) string {
	value := os.Getenv(key)
	if value == "" {
		log.Fatalf("environment variable %s is required", key)
	}

	return value
}

func getEnv(key, fallback string) string {
	value := os.Getenv(key)
	if value == "" {
		return fallback
	}

	return value
}

func mustDurationMinutes(key string, fallback int) time.Duration {
	raw := getEnv(key, strconv.Itoa(fallback))
	minutes, err := strconv.Atoi(raw)
	if err != nil || minutes <= 0 {
		log.Fatalf("environment variable %s must be a positive integer", key)
	}

	return time.Duration(minutes) * time.Minute
}
