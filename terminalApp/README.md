## Klassid:   
<pre>
    <b>Osaleja</b>
    int id                      // osaleja ainulaadne id
    String nimi                 // osaleja nimi
    float ELO                   // osaleja ELO, põhineb tema kohast edetabelites.
    List<Edetabel> edetabelid   // Edetabeli objektid, kus osaleja on osalenud.
    Edetabel ELOedetabel        // ELO edetabeli objekt.
    boolean juhendaja           // kas osaleja on juhendaja või mitte.
  
    <b>Edetabel</b>
    int id                      // edetabeli ainulaadne id
    String nimi                 // edetabeli nimi
    List<String> osalejad       // edetabelis olevate osalejate nimed.
    List<String> tulemused      // osalejate tulemused Kui osaleja on massiivis 3. kohal siis tema skoor on ka 3. kohal.
    String skooriÜhik           // skoori ühik, (ms, p, ...)
</pre>

## Funktsioonid:
- **PHXC.java (peamine klass)**
    - `PHXCInit()` : kaabitseb phxc.ee lehekülje andmed ja valmistab ette osalejate objektid ning edetabelite objektid.
    - `koostaELOEdetabel(int id, Osaleja[] osalejad)` : koostab ELOedetabeli
    - `kasOnNumber(String str)` : kontrollib kas antud sõne on number või mitte.
    - `leiaOsaleja(String nimi)` : leiab osaleja objekti antud nime kaudu.
      
- **Kaabitseja.java**
    - `kaabitseLehekülge(String link)` : requesti kaudu tõmbab lehekülje andmed
    - `LeiaEdetabeliteAndmed(HttpResponse<String> data)` : leiab httpresponse-st kõik edetabeli andmed välja.
 
- **Osaleja.java**
    - `setELOedetabel(Edetabel uus)` : seadistab ELOedetabeli muutuja.
    - `getNimi()` : tagastab osaleja nime
    - `getELO()` : tagastab osaleja ELO
    - `getId()` : tagastab osaleja id
    - `easeInCubic(double x)` : ease funktsioon ELO arvutamiseks.
    - `arvutaELO()` : arvutab osaleja ELO põhinedes tema kohale edetabelites.
    - `lisaEdetabel(Edetabel edetabel)` : lisab edetabeli osaleja edetabelite listi. 
      
- **Edetabel.java**
    - `getNimi()` : tagastab edetabeli nime.
    - `getSkooriÜhik()` : tagastab edetabeli skooriühiku
    - `getOsalejad()` : tagastab osalejate listi, kus on kõigi edetabelis osalejate nimed.
    - `setOsaleja(List<String> osalejad)` : seadistab osalejad muutuja
    - `setTulemused(List<String> tulemused)` : seadistab tulemused muutuja
    - `leiaOsalejaKoht(String nimi)` : tagastab osaleja koha edetabelis
    - `leiaOsalejaKohtIlmaJuh(String nimi)` : tagastab osaleja koha edetabelis (juhendajad välja võetud)
    - `leiaOsalejaKohtJuh(String nimi)` : tagastab osaleja koha edetabelis (ainult juhendajad)
    - `leiaOsalejaTulemus(String nimi)` : leiab osaleja tulemuse nime kaudu
    - `leiaOsalejaTulemus(int koht)` : leiab osaleja tulemuse koha kaudu
