package main

import (
	"context"
	"errors"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"anxiety-backend/internal/auth"
	"anxiety-backend/internal/config"
	"anxiety-backend/internal/database"
	httpapi "anxiety-backend/internal/http"
	"anxiety-backend/internal/notifications"
	"anxiety-backend/internal/thoughts"
	"anxiety-backend/internal/users"
)

func main() {
	cfg := config.MustLoad()

	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	db, err := database.ConnectWithRetry(ctx, cfg.DatabaseURL, 10, 2*time.Second)
	if err != nil {
		log.Fatalf("connect database: %v", err)
	}
	defer db.Close()

	userRepo := users.NewRepository(db)
	thoughtRepo := thoughts.NewRepository(db)
	notificationRepo := notifications.NewRepository(db)
	tokenManager := auth.NewJWTManager(cfg.JWTSecret, cfg.JWTIssuer, cfg.JWTTokenTTL)
	authService := users.NewAuthService(userRepo, tokenManager)
	thoughtService := thoughts.NewService(thoughtRepo)
	notificationService := notifications.NewService(notificationRepo, thoughtRepo)

	handler := httpapi.NewServer(authService, thoughtService, notificationService, tokenManager)

	server := &http.Server{
		Addr:              ":" + cfg.Port,
		Handler:           handler,
		ReadHeaderTimeout: 5 * time.Second,
	}

	go func() {
		log.Printf("backend started on :%s", cfg.Port)
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatalf("http server: %v", err)
		}
	}()

	<-ctx.Done()

	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := server.Shutdown(shutdownCtx); err != nil {
		log.Printf("graceful shutdown error: %v", err)
	}
}
