# PHXCsb
PHXC frontendi jaoks API.

## Endpointid
- **/edetabel/{edetabeli_nimi}?type=[all|last]** - tagastab edetabeli osalejate andmed. all tagastab kõik timestampid, last uusima timestampiga.
- **/osalejateEdetabel** - kõikide osalejate andmed.
- **/osalejateEdetabel/{osaleja_nimi}** - üksiku osaleja andmete otsimine osaleja nime kaudu
- **/ELO** - ELO edetabel
- **/ulesanded** - edetabeli nimi koos edetabeli osalejate arvuga.

## Ülesseadistamine:
Ülesseadistamiseks läheb vaja andmebaasi, kus on tabelid **data**, **dataFinal**, **edetabelid**. 

Veel peab koostama **src/main/resources/application.properites** faili.


### Linux:

1. Saa õiged kredentsiaalid ja pane need `SpringBoot/src/main/resources/application.properties` faili

2. 
```bash
cd /path/to/oop/SpringBoot

# vajadusel
sudo apt install openjdk-17-jdk
export $JAVA_HOME=/usr/lib/jvm/java-17-open

mvn clean install

java -jar target/PHXCsb-0.0.1-SNAPSHOT.jar
```

### Windows:
1. Saa õiged kredentsiaalid ja pane need `SpringBoot/src/main/resources/application.properties` faili
2. Jooksuta PhxCsbApplication.java


### Andmebaasi struktuur
```SQL
  -- data tabel on /edetabel/{nimi} andmete jaoks. Siia tuleb andmeid iga kindla ajavahemiku tagant.
  data (
    edetabel_id - int(11)
    edetabel_nimi - varchar(30)
    osaleja - varchar(30)
    aeg - timestamp
    skoor - varchar(255)
    skoor2 - float
  )

  -- dataFinal on kasutusel /osalejateEdetabel, et osalejate kohad arvutada. 
  dataFinal (
    edetabel_id - int(11)
    edetabel_nimi - varchar(30)
    osaleja - varchar(30)
    aeg - timestamp
    skoor - varchar(255)
    skoor2 - float
  )

  -- edetabelid tabel on kaabitseja deadline kontrollmiseks ning edetabeli sortimise leidmiseks.
  edetabelid (
    edetabel_nimi - varchar(30)
    deadline - timestamp
    sortimine - varchar(255)
  )
```

### application.properties
```PROPERTIES
spring.application.name=PHXCsb
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=<andmebaasiURL>
spring.datasource.username=<andmebaasiKasutajatunnus>
spring.datasource.password=<andmebaasiParool>
spring.datasource.driver-class-name=<andmebaasidriver>  //mysql oma: com.mysql.cj.jdbc.Driver
spring.jpa.open-in-view=false

# maxLifeTime value peab olema väiksem kui "wait_timeout" mysql instance-l.
# "wait_timeout" on sekundites.
# "maxLifeTime" on ms.
spring.datasource.hikari.maxLifetime=590000
spring.datasource.hikari.keepaliveTime=60000
```
