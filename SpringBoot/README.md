# PHXCsb
PHXC frontendi jaoks API.

# Endpointid
- **/edetabel/{edetabeli_nimi}?type=[all|last]** - tagastab edetabeli osalejate andmed. all tagastab kõik timestampid, last uusima timestampiga.
- **/osalejateEdetabel** - kõikide osalejate andmed.
- **/osalejateEdetabel/{osaleja_nimi}** - üksiku osaleja andmete otsimine osaleja nime kaudu
- **/ELO** - ELO edetabel
- **/ulesanded** - edetabeli nimi koos edetabeli osalejate arvuga.

# Ülesseadistamine:

Linux-il:

1. Saa õiged kredentsiaalid ja pane need `SpringBoot/src/main/resources/application.properties` faili

2. 
```bash
cd /path/to/oop/SpringBoot

# vajadusel
sudo apt install openjdk-17-jdk
export #JAVA_HOME=/usr/lib/jvm/java-17-open

mvn clean install

java -jar target/PHXCsb-0.0.1-SNAPSHOT.jar
```
