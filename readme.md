# oop projekt - xhcp lehe kaabitseja

Autorid: Rainer Vana, Kevin Akkermann

## Programmi eesmärk

Aines Programmeerimine II (LTAT.03.007) on näha iga ülesande puhul edetabel. Seal näeb iga ülesande kohta eraldi edetabelit, kuid puudub nende andmete visualiseerimise võimalus. 




lõppprodukti ideed:

* võimaldab vaadata mingi ELO süsteemi järgi parimaid
* võimaldab vaadata iga kasutaja performancete kokkuvõtet
* saab vaadata mingi ülesannete edetabelit iga 5 minuti tagant? (nõuab cron jobi, mis kirjutab db-sse tolle hetke seisu)
* contest management system laadne koha displaymine per ülesanne?
* vb auth, et kui suudad sisse logida õis vms, ss saad näha ka teisi, kes on oma nime ühendanud pseudoga?

db struktuur:

contests:

| id | nimi | jrk nr |
| -- | ---- | ------ |
| PK int | varchar(50) | serial |

users: 
| id | pseudo | real name |
| -- | ------ | --------- |
| PK serial | varchar(20) | varchar(40) |

results:
| contestID | timestamp | results |
| --------- | --------- | ------- |
| FK from contests | ISODateString | JSON | 

results JSON struktuur:
```js
[
    {
        rank: number,
        pseudoname: string,
        runtime: float
    },
    {
        rank: number,
        pseudoname: string,
        runtime: float
    },
    // ...
]

```


