# BullsAndCowsBot
Telegram bot to play  [Bulls&amp;Cows](https://en.wikipedia.org/wiki/Bulls_and_cows) game

## .env sample
Required environmental variables:
```dotenv
TGBOT_TOKEN=
SERVER_PORT=
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

## prepare vm
create bare git repository
```bash
mkdir ~/git/tgbot-bac.git
cd ~/git/tgbot-bac.git
git init --bare
git config --global init.defaultBranch master
```
create hook
```bash
touch hooks/post-receive
```
set following hook content
```bash
#!/bin/bash
while read oldrev newrev ref
do
  if [[ $ref =~ .*/master$ ]];
  then
    echo "Master ref received. Deploying master branch to production..."
    git --work-tree=/var/www/tgbot-bac --git-dir=/home/<VM_USER>/git/tgbot-bac.git checkout -f
  else
    echo "Ref $ref successfully received. Doing nothing: only the master branch may be deployed on this server."
  fi
done
```
activate hook
```bash
chmod +x hooks/post-receive
```
add permissions for www
```bash
sudo mkdir /var/www/tgbot-bac
sudo chown -R <VM_USER>:<VM_USER> /var/www/tgbot-bac
```


## configure remote repository
```bash
git remote add deploy ssh://<VM_USER>@<VM_IP>/home/<VM_USER>/git/tgbot-bac.git
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

