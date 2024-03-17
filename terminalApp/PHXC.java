package terminalApp;
import java.net.http.HttpResponse;
import java.util.*;

public class PHXC {
    /**
     * Lisab osaleja osalejad massiivi.
     * @param osalejad - antud massiiv
     * @param osaleja - antud terminalApp.Osaleja objekt
     * @return - uue massiivi, kus on vanad elemndid koos uuega.
     */
    public static Osaleja[] lisaOsaleja(Osaleja[] osalejad, Osaleja osaleja) {
        Osaleja[] uus = new Osaleja[osalejad.length + 1];

        System.arraycopy(osalejad, 0, uus, 0, osalejad.length);
        uus[osalejad.length] = osaleja;

        return uus;
    }

    /**
     * Kontrollib kas osaleja on juba lisatud osalejate massiivi
     * @param osalejad - antud massiv
     * @param osaleja - antud terminalApp.Osaleja objekt
     * @return - true/false selle põhjal kas on juba lisatud.
     */
    public static boolean osalejaJubaLisatud(Osaleja[] osalejad, Osaleja osaleja) {
        for (Osaleja o : osalejad) {
            if (o.getNimi().equals(osaleja.getNimi())) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        // scrape website
        HttpResponse<String> unparsedData = Scraper.scrapeWebsite("http://www.phxc.ee");

        // Massiivid
        String[][] osalejateNimed = Parser.leiaOsalejad(unparsedData);
        String[][] osalejateSkoorid = Parser.leiaTulemused(unparsedData);
        String[] edetabeliNimed = Parser.leiaEdetabeliNimed(unparsedData);
        Edetabel[] edetabelid = new Edetabel[edetabeliNimed.length];

        // Lisa osalejad massiivi.
        Osaleja[] osalejad = new Osaleja[0];
        int id = 0;
        for (String[] osalejadEdetabelis : osalejateNimed) {
            for (String osalejaNimi : osalejadEdetabelis) {
                boolean juhendaja = false;
                if (osalejaNimi.contains("(Juh)")){
                    juhendaja = true;
                    osalejaNimi = osalejaNimi.substring(0, osalejaNimi.length()-6);
                }

                Osaleja osaleja = new Osaleja(id, osalejaNimi, juhendaja);

                if (!osalejaJubaLisatud(osalejad, osaleja)) {
                    osalejad = lisaOsaleja(osalejad, osaleja);
                }
                id++;
            }
        }

        // Koosta edetabeli klassid ja lisa osalejad sinna.
        int i = 0;
        for (String edetabeliNimi : edetabeliNimed) {
            Edetabel edetabel = new Edetabel(i, edetabeliNimi);

            for (String osalejaNimi : osalejateNimed[i]) {
                for (Osaleja osaleja : osalejad) {
                    if (osaleja.getNimi().equals(osalejaNimi)) {
                        edetabel.lisaOsaleja(osaleja);
                        osaleja.lisaEdetabelise(edetabel);
                        break;
                    }
                }
            }
            for (String osalejaSkoor : osalejateSkoorid[i]) {
                edetabel.lisaSkoor(Float.parseFloat(osalejaSkoor));
            }

            edetabelid[i] = edetabel;
            i++;
        }

        // Arvuta osalejate ELO
        for (Osaleja osaleja : osalejad) {
            osaleja.arvutaELO();
        }

        boolean run = true;
        while (run) {
            Scanner sisend = new Scanner(System.in);
            System.out.print("[1] - vaata ülesande edetabelit\n[2] - ELO edetabel\n[3] - leia osaleja\n[x] - exit\n");
            String tekst = sisend.nextLine().toLowerCase();

            switch (tekst) {
                // edetabeli näitamine
                case "1":
                    System.out.println("Millist edetabelit soovite näha?");
                    int number = Integer.parseInt(String.valueOf(sisend.nextLine()));
                    Edetabel edetabel = edetabelid[number - 1];
                    System.out.println(edetabel);

                    break;

                // ELO edetabel
                case "2":
                    System.out.println("[1] - kõik\n[2] - ilma juh.\n[3] - ainult juh.");
                    int filterELO = Integer.parseInt(String.valueOf(sisend.nextLine()));

                    System.out.println();
                    System.out.println("[ ELO ]");
                    HashMap<Integer, String> map = new HashMap<>();
                    for (Osaleja osaleja : osalejad) {
                        // Ilma juh.
                        if (filterELO == 2){
                            if (osaleja.isJuhendaja()) continue;
                        }
                        // Ainult juh
                        if (filterELO == 3){
                            if (!osaleja.isJuhendaja()) continue;
                        }

                        String nimi = osaleja.getNimi();
                        int ELO = osaleja.getELO();

                        map.put(ELO, nimi);
                    }

                    Map<Integer, String> treeMap = new TreeMap<>(map);
                    Object[] ELOd = treeMap.keySet().toArray();
                    Object[] nimed = treeMap.entrySet().toArray();
                    int koht = 1;
                    for (int j = ELOd.length - 1; j >= 0; j--) {
                        String nimi = String.valueOf(nimed[j]).split("=")[1];
                        String vahed = " ".repeat(25 - nimi.length());
                        System.out.printf("%02d. %s%s%s%n", koht, nimi, vahed, ELOd[j]);
                        koht++;
                    }
                    System.out.println();
                    break;

                // osaleja otsimine
                case "3":
                    System.out.print("Osaleja: ");
                    String nimi = String.valueOf(sisend.nextLine());
                    Osaleja otsitav = null;

                    for (Osaleja o : osalejad) {
                        if (Objects.equals(o.getRealNimi(), nimi)) {
                            otsitav = o;
                            break;
                        }
                    }

                    System.out.println();
                    if (otsitav != null) {
                        System.out.println(otsitav);
                    } else {
                        System.out.println("Ei leidnud sellise nimega osalejat.");
                    }
                    System.out.println();
                    break;

                // exit.
                case "x":
                    run = false;
                    break;
            }
        }
    }
}

