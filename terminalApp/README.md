## Klassid:   
<pre>
    <b>Osaleja</b>
    int id                  // osaleja ainulaadne id
    String nimi             // osaleja nimi
    int ELO                 // osaleja ELO, põhineb tema kohast edetabelites.
    Edetabel[] edetabelid   // Edetabeli objektid, kus osaleja on osalenud.
    boolean juhendaja       // kas osaleja on juhendaja või mitte.
  
    <b>Edetabel</b>
    int id                  // edetabeli ainulaadne id
    String nimi             // edetabeli nimi
    Osaleja[] osalejad      // edetabelis olevate osalejate objektid.
    float[] skoorid         // osalejate skoorid. Kui osaleja on massiivis 3. kohal siis tema skoor on ka 3. kohal.
</pre>

## Funktsioonid:
- **PHXC.java (peamine klass)**
    - `osalejaJubaLisatud(Osaleja[] osalejad, Osaleja osaleja)` : tagastab true/false põhinedes sellel kas osaleja objekt on juba lisatud massiivi.
    - `lisaOsaleja(Osaleja[] osalejad, Osaleja osaleja)` : lisab Osaleja objekti osalejate massiivi. NB! tagastab uue massiivi.
 
      
- **Scraper.java**
    - `scrapeWebsite(String link)` : kaabitseb lehe külje ja tagastab HttpResponse-i.
 
      
- **Parser.java**
    - `lisaMassiivi(String[] massiiv, String element)` : koostab uue massiivi, kuhu lisab vanad elemendid, koos uuega. Tagastab uue massiivi.
    - `leiaEdetabeliNimed(HttpResponse<String> data)` : leiab httpresponse-st kõik edetabeli nimed välja ja tagastab massiivi kus kõik nimed on.
    - `leiaOsalejad(HttpResponse<String> data)` : leiab httprespone-st kõik osalejad, k.a arvatud duplikaadid.
    - `leiaTulemused(HttpResponse<String> data)` : leiab httprespone-st kõik tulemused, k.a arvatud duplikaadid.
 
      
- **Osaleja.java**
    - `getNimi()` : tagastab osaleja nime, aga kui on juhendaja, siis tagastab "nimi + (Juh)".
    - `getRealNimi()` : tagastab nime.
    - `getEdetabelid()` : tagastab edetabelite massiivi. Massiivis on ainult need edetabelid kus osaleja osaleb.
    - `getELO()` : tagastab osaleja ELO.
    - `isJuhendaja()` : tagastab true/false põhinedes sellel kas osaleja on juhendaja.
    - `lisaEdetabelisse(Edetabel edetabel)` : lisab Edetabeli objekti osaleja edetabelid massiivi
    - `arvutaELO()` : arvutab osaleja ELO, kuidas arvutatakse on funktsioonis endas kirjas.
    - `toString()` : tagastab osaleja info.
 
      
- **Edetabel.java**
    - `getId()` : tagastab edetabeli ainulaadse id.
    - `getNimi()` : tagastab edetabeli nime.
    - `getOsalejad()` : tagastab osalejate massiivi, kus on kõigi edetabelis osalejate objektid.
    - `getSkoorid()` : tagastab skooride massiivi, kus on kõigi edetabelis osalejate skoorid.
    - `toString()` : tagastab edetabeli info.
    - `lisaOsaleja(Osaleja osaleja)` : lisab osaleja selle edetabeli osalejate massiivi, kui juba lisatud ei ole. Ehk duplikaate tekkida ei saa.
    - `leiaOsalejaSkoor(Osaleja osaleja)` : tagastab osaleja skoori edetabelis.
    - `leiaOsalejaKoht(Osaleja osaleja, int type)` : tagastab osaleja koha edetabelis. (type: 0 - kõik osalejad, 1 - ilma juh., 2 - ainult juh.)
    - `leiaOsaleja(String osalejaNimi)` : tagastab Osaleja objekti tema nime kaudu.

