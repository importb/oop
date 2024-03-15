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

plaanis veel lisada:
osaleja, edetabeli klassid.


Osaleja:
* String nimi;             - osaleja nimi.
* Edetabel[] edetabelid;   - näitab millistes edetabelites osales
* Float[] skoorid;         - osaleja skoorid erinvates edetabelites. nt kui osales 1. ja 3., siis massiiv näeks välja [4.5, 0, 123.4] vms.


Edetabel:
* int id;              - edetabeli id
* String nimi;         - edetabeli nimi
* Osaleja[] osalejad   - edetabeli osalejad.


loomulikult struktuur võib muutuda, aga see praegune idee.
