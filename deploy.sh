#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "🔄 1. Подтягиваем свежие изменения из GitHub (ветка Develop)..."
git pull origin Develop

echo "🛑 2. Останавливаем и удаляем старый контейнер (если он запущен)..."
docker rm -f autokaif_bot 2>/dev/null || true
docker compose down 2>/dev/null || true

echo "🏗️ 3. Собираем свежий образ и запускаем бота..."
docker compose up -d --build --force-recreate

echo "🧹 4. Удаляем старые неиспользуемые образы (экономим место на VPS)..."
docker image prune -f

echo "✅ Статус контейнера:"
docker compose ps

echo "📋 Логи бота (для выхода из логов нажми Ctrl+C — сам бот продолжит работать):"
docker compose logs -f --tail=30 autokaif-bot
