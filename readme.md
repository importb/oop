# oop projekt - phxc lehe kaabitseja

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

edetabelid:

| nimi | deadline |
| ---- | ------ |
| varchar(50) | datetime |


data: (kaabitsetud andmed)
| edetabel_id | edetabel_nimi | osaleja | skoor | aeg |
| ----------- | ------------- | ------- | ----- | --- |
| int(11) | varchar(30) | varchar(30) | float | timestamp |

users (TODO hiljem): 
| id | pseudo | real name |
| -- | ------ | --------- |
| PK serial | varchar(20) | varchar(40) |

## Kaabitseja

Java programm, mis kaabitseb lehelt aktiivsete edetabelite tulemusi iga 5 minuti tagant. Täpsemalt [alamkaustas](kaabitseja/README.md)


## terminalApp
Programm, mis kaabitseb phxc lehekülje.
Programmis saab vaadelda edetabeleid, üksikute osalejate tulemusi, millistest edetabelites nad osalenud on, mis nende ELO on ning millised nende tulemused on edetabelites olnud.

Klasside ja funktsioonide info: 
[Klassid, funktsioonid](terminalApp/README.md)
