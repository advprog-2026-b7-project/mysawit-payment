# MySawit Payment Service

Service ini menangani sistem penggajian (*payroll*) secara asinkronus dan terintegrasi dengan event-driven architecture. Service jalan di port `8082` (default) dan menggunakan database PostgreSQL terpisah.

## Menjalankan Dengan Infra Repo

1. Jalankan infra repo (PostgreSQL & RabbitMQ via Docker).
2. Pastikan database untuk payment tersedia, default nama DB: `mysawit_payment`.
3. Jalankan service ini.

## Konfigurasi Database & Broker

Service ini membaca konfigurasi dari environment variable:

- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/mysawit_payment`)
- `SPRING_RABBITMQ_HOST` (default: `localhost`)
- `SPRING_DATASOURCE_USERNAME` (default: `postgres`)
- `SPRING_DATASOURCE_PASSWORD` (default: `postgres`)

## Menjalankan Service

### Local (tanpa Docker)
```bash
./gradlew bootRun