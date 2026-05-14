FROM golang:1.22-alpine AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .
RUN go build -o /backend-app ./main.go

FROM alpine:3.20

WORKDIR /app

COPY --from=builder /backend-app /usr/local/bin/backend-app

EXPOSE 8080

CMD ["backend-app"]
