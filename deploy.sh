#!/bin/bash
#при первой же ошибке останавливает запуск
set -e
#переходит в папку где лежит сам скрипт $0 путь к скрипту dirname отрезает от пути сам файл
cd "$(dirname "$0")"
#обновляем проект и делем обычное обновление без merge комитов
echo "обновляем develop"
git pull --ff-only origin Develop
#создает образ и запускает контейнер в фоне, если код изменился compose сам пересоздаст контейнер
echo "Соборка свежего образа и запуск бота..."
docker compose up -d --build
#удаляем старые образы пересборки
echo "Удаление старых неиспользуемых образов"
docker image prune -f

echo "Статус контейнера:"
docker compose ps

echo "Логи:"
docker compose logs -f --tail=30 autokaif-bot
