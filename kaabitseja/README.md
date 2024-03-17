# kaabitseja

Mikroteenus, mis iga 5 minuti tagant kaabib http://phxc.ee lehelt andmed ja kirjutab andmed MySQL andmebaasi.

## Kuidas lokaalselt jooksutada?

### Linux / WSL


0. Vajadusel installi Java
1. Hangi õige `.env` fail ja anna talle väärtused:
1.1. MYSQL_HOST (url ilma jdbc:mysql:// osata)
1.2. MYSQL_PORT
1.3. MYSQL_USERNAME
1.4. MYSQL_PASSWORD
1.5. MYSQL_NAME (andmebaasi nimi)
2. `chmod +x build.sh run.sh`
3. `./build.sh && ./run.sh`

### Windows

Ma ei tea mulle ei meeldi Windows. Aga mysql-connector ja org.json jar failid peab andma Java classpathi.

## Kuidas konteinerit üles saada?

0. Vajadusel installi Docker (duh...)
1. `docker build -t kaabitseja .`, võib võtta kuni 5 minutit, kuna openjdk ja ubuntu:latest on suured
2. `docker run -d kaabitseja` / `docker run -d --name kaabitseja_konteiner kaabitseja`

`docker exec -it <konteineri ID> bash`, et saada terminali ligipääs konteinerile
`docker stop <konteineri ID>`, et konteiner peatada
`docker rm <konteineri ID>`, et konteiner kustutada

## Mis on kus konteineris?

`/app/` on peakaust, seal on Main.java ja Main.class failid + jar lisad. Tekstilised logid kirjutatakse `/app/data/` kausta, faili nimes on kirjutamise aeg

Logisid saab vaadata /var/log/cron.log failist

TODO: dockeri volüümide peale salvestada data_*.txt failid, et need jääks ka restartides