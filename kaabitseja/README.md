# kaabitseja

Mikroteenus, mis iga 5 minuti tagant kaabib http://phxc.ee lehelt andmed ja kirjutab andmed MySQL andmebaasi. See konteiner jookseb AWS EC2 Ubuntu virtuaalmasinas.

## Klasside ja meetodite kirjeldused:

- `String[] lisaMassiivi(String[] massiiv, String element)` - abimeetod, mis lisab sõneMassivi uue elemendi

- `Map<String, String> readEnvFile(String kaust)` - meetod, mis loeb sisse kõik .env failis olevad keskkonnamuutujad (MYSQL-iga ühendumiseks) ja tagastab Map'i

- `HttpResponse<String> scrapeWebsite(String link)` - meetod, mis saadab päringu link muutujas olevale lehele

- `void writeDataToDatabase(ArrayList<String> data, String kaust)` - meetod, mis võtab argumendina kaabitsetud andmed ArrayListina ning kirjutab need andmed MySQL andmebaasi. Enne kirjutamist kontrollime, kas kindel edetabel on veel lahti ning suletud edetabeli tulemusi me ei kirjuta üle.

- `void main(String[] args)` - peameetod, mis kaabib kõik andmed lehel, muudab need JSON-iks, kirjutab faili ja seejärel andmebaasi.


## Kuidas lokaalselt jooksutada?

### Linux / WSL


0. Vajadusel installi Java
1. Hangi õige `.env` fail ja anna talle väärtused:
    1. MYSQL_HOST (url ilma jdbc:mysql:// osata)
    2. MYSQL_PORT
    3. MYSQL_USERNAME
    4. MYSQL_PASSWORD
    5. MYSQL_NAME (andmebaasi nimi)
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