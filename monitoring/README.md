# Мониторинг приложения

## Структура

- `prometheus.yml` - конфигурация Prometheus для сбора метрик
- `grafana/datasources/` - конфигурация источников данных (Prometheus)
- `grafana/dashboards/` - конфигурация и дашборды для Grafana
  - `dashboard.yml` - конфигурация provisioning дашбордов
  - `json/backend-metrics.json` - дашборд с метриками GC и Response Time

## Использование

1. Запустите контейнеры: `docker compose up -d`
2. Откройте Grafana: http://localhost:3000
   - Логин: admin (или значение из переменной GRAFANA_ADMIN_USER)
   - Пароль: admin (или значение из переменной GRAFANA_ADMIN_PASSWORD)
3. Откройте Prometheus: http://localhost:9090

## Если дашборды не отображаются

1. Перезапустите Grafana: `docker compose restart grafana`
2. Проверьте логи: `docker compose logs grafana | grep -i dashboard`
3. Убедитесь, что Prometheus datasource подключен (Configuration -> Data Sources)
4. Проверьте, что backend приложение запущено и доступно по адресу `sber__backend:8080`
