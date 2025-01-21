# BullsAndCowsBot
Telegram bot to play  [Bulls&amp;Cows](https://en.wikipedia.org/wiki/Bulls_and_cows) game

## .env sample
Required environmental variables:
```dotenv
TGBOT_TOKEN=
TGBOT_PROXY_HOSTNAME=
TGBOT_PROXY_PORT=
TGBOT_PROXY_USERNAME=
TGBOT_PROXY_PASSWORD=
DB_NAME=
DB_HOST=
SPRING_DATASOURCE_PASSWORD=
SPRING_DATASOURCE_USERNAME=
GITHUB_TOKEN=
```

## telegram commands
```
new - Начать новую игру
info - Отобразить подсказку по правилам игры
score - Посмотреть результаты
```

## deploy to vm
there are following steps:
1. merge to `master` branch
2. push to `deploy` remote repo
3. run docker compose
```bash
docker compose up --detach
```

## links
* [Emoji Unicode Tables](https://apps.timwhitlock.info/emoji/tables/unicode)
* 

