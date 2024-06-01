# oop projekt - phxc lehe kaabitseja

Külasta lehte: [https://phxc2.ee](https://phxc2.ee)

Autorid: Rainer Vana, Kevin Akkermann

## Programmi eesmärk

Aines Programmeerimine II (LTAT.03.007) on näha iga ülesande puhul edetabel. Seal näeb iga ülesande kohta eraldi edetabelit, kuid puudub nende andmete visualiseerimise võimalus. 

Võimalused

* Võimaldab vaadata ELO süsteemi järgi parimaid
* Võimaldab vaadata iga kasutaja tulemuste kokkuvõtet
* Saab vaadata mingi ülesannete edetabelit iga 5 minuti tagant. Selleks saab animeerida edetabeleid ja näha, kes millal esitas oma lahenduse jms


## Andmebaasi struktuur:

**edetabelid**:

| nimi        | deadline | sortimine    | 
| ----        | ------   | ---          |
| varchar(50) | datetime | varchar(255) |

> kasutades sortimine veeru väärtust sorteeritakse ELO jms päringutega tulemusi. Näiteks on sortimine ",d.skoor * 1 ASC", ",d.skoor * 1 DESC, d.skoor2 * 1 ASC"

**dataNew**: (kaabitsetud andmed)
| edetabel_id | edetabel_nimi | osaleja     | skoor | skoor2        | aeg       |
| ----------- | ------------- | -------     | ----- | ---           | ---       |
| int(11)     | varchar(30)   | varchar(30) | float | float \| null | timestamp |

**dataFinal**: (lõpptulemused)
| edetabel_id | edetabel_nimi | osaleja     | skoor | skoor2        | aeg       | koht    |
| ----------- | ------------- | -------     | ----- | ---           | ---       | ---     |
| int(11)     | varchar(30)   | varchar(30) | float | float \| null | timestamp | int(11) |


## Kaabitseja

Java programm, mis kaabitseb lehelt aktiivsete edetabelite tulemusi iga 5 minuti tagant. Täpsemalt [alamkaustas](kaabitseja/README.md)


## terminalApp
Programm, mis kaabitseb phxc lehekülje.
Programmis saab vaadelda edetabeleid, üksikute osalejate tulemusi, millistest edetabelites nad osalenud on, mis nende ELO on ning millised nende tulemused on edetabelites olnud.

Klasside ja funktsioonide info: 
[Klassid, funktsioonid](terminalApp/README.md)

# Rühmatöö 2

Kolmekihiline Next.js / Spring Boot / MySQL rakendus, mis näitab kokkuvõtteid terve kursuse peale

## API endpointid

### 1. ELO edetabeli visualiseerimine

Potentsiaalne lahendus:
* API endpoint GET /users
* Iga nädala lõpus kirjutame uude DB tabelisse uuesti arvutatud ELO seisu (nt Cron-iga)
* Tagastab: 

```js
[
    {
        pseudoname: <String>,
        ELO: <Float>,
        results: [
            {
                edetabelNimi: <String>,
                skoor1: <Float>,
                skoor1Unit: <String>,
                skoor2: <Float | NULL>,
                skoor2Unit: <String | NULL> 
            },
            // ...
        ]
    },
    {
        pseudoname: <String>,
        ELO: <Float>,
        results: [
            // ...
        ]
    },
    // .....
]
```

### 2. Ühe kindla kasutaja tulemused

Potentsiaalne lahendus:
* API endpoint GET /users/{pseudo}
* Getib andmebaasist kõik selle kasutaja tulemused (igast edetabelist, vali viimane timestamp ja sorteeri kõik tulemused ning ütle, mitmes see kasutaja on)
* Tagastab:

```js
{
    pseudoname: <String>,
    ELO: <Float>,
    results: [
        {
            edetabelNimi: <String>,
            skoor1: <Float>,
            skoor1Unit: <String>,
            skoor2: <Float | NULL>,
            skoor2Unit: <String | NULL> 
        },
        // ...
    ]
},
```

### 3. Ühe edetabeli ajas visualiseerimine

Kuna iga 5 minuti tagant on scrapetud, siis saame näidata animatsioonidega, kuidas ajas need tulemused muutusid

Potentsiaalne lahendus:
* API endpoint GET /results/{edetabel_nimi}?type=<all | last>
* last tagastab ainult viimase timestampi
* Tagastab kasvavas järjekorras timestampidega:

```js
[
    {
        timestamp: <UNIX timestamp>,
        results: [
            {
                pseudo: <String>,
                skoor1: <Float>,
                skoor2: <Float | NULL>,
            },
            {
                pseudo: <String>,
                skoor1: <Float>,
                skoor2: <Float | NULL>,
            },
        ]
    }
]
```
