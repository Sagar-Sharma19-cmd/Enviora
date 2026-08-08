# Monitoring & Observability Blueprint

- **Metrics**: Spring Boot Actuator exposing Prometheus metrics at `/actuator/prometheus`.
- **Collector**: Prometheus scraper configured in `infra/monitoring/prometheus.yml`.
- **Dashboards**: Grafana setup for JVM metrics, HTTP latency, and DB connection pool stats.
