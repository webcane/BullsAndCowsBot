# BullsAndCowsBot
Telegram bot to play  [Bulls&amp;Cows](https://en.wikipedia.org/wiki/Bulls_and_cows) game

## .env sample
Required environmental variables:
```dotenv
TGBOT_TOKEN=
DB_NAME=
DB_HOST=
SPRING_DATASOURCE_PASSWORD=
SPRING_DATASOURCE_USERNAME=
GITHUB_TOKEN=
```

## proxy
To run the tgbot over proxy define additional environmental variables:
```dotenv
TGBOT_PROXY_HOSTNAME=
TGBOT_PROXY_PORT=
TGBOT_PROXY_USERNAME=
TGBOT_PROXY_PASSWORD=
```

## logging
If necessary, change the logging level using following environmental variables:
```dotenv
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_JDBC=DEBUG
LOGGING_LEVEL_CANE_BROTHERS_TGBOT=DEBUG
```

## telegram commands
```
new - Start new game
info - Show game rules
score - Show game score
settings - Game Settings
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

