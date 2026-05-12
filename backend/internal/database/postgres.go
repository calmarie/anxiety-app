package database

import (
	"context"
	"fmt"
	"time"

	"github.com/jackc/pgx/v5/pgxpool"
)

func ConnectWithRetry(ctx context.Context, databaseURL string, attempts int, delay time.Duration) (*pgxpool.Pool, error) {
	var lastErr error

	for i := 0; i < attempts; i++ {
		pool, err := pgxpool.New(ctx, databaseURL)
		if err == nil {
			pingCtx, cancel := context.WithTimeout(ctx, 5*time.Second)
			lastErr = pool.Ping(pingCtx)
			cancel()
			if lastErr == nil {
				return pool, nil
			}
			pool.Close()
		} else {
			lastErr = err
		}

		select {
		case <-ctx.Done():
			return nil, ctx.Err()
		case <-time.After(delay):
		}
	}

	return nil, fmt.Errorf("database unavailable after %d attempts: %w", attempts, lastErr)
}
