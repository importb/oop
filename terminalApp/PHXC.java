package terminalApp;

import java.net.http.HttpResponse;
import java.util.*;

public class PHXC {
    /**
     * ELO edetabeli koostamine
     * @param id - edetabeli id
     * @param osalejad - osalejate massiiv
     * @return - ELO edetabel
     */
    public static Edetabel koostaELOEdetabel(int id, Osaleja[] osalejad) {
        HashMap<Integer, String> ELOMap = new HashMap<>();
        Edetabel ELOedetabel = new Edetabel(id, "ELO", "p");
        for (Osaleja osaleja : osalejad) {
            osaleja.arvutaELO();

            ELOMap.put(osaleja.getELO(), osaleja.getNimi());
        }
        Map<Integer, String> ELOtreeMap = new TreeMap<>(ELOMap);
        Object[] ELOd = ELOtreeMap.keySet().toArray();
        Object[] nimed = ELOtreeMap.entrySet().toArray();

        for (int j = ELOd.length - 1; j >= 0; j--) {
            String nimi = String.valueOf(nimed[j]).split("=")[1];

            for (Osaleja osaleja : osalejad) {
                if (osaleja.getNimi().equals(nimi)) {
                    ELOedetabel.lisaOsaleja(osaleja);
                    ELOedetabel.lisaSkoor(Float.parseFloat(String.valueOf(ELOd[j])));
                }
            }
        }

        return ELOedetabel;
    }

    /**
     * Lisab osaleja osalejad massiivi.
     *
     * @param osalejad - antud massiiv
     * @param osaleja  - antud terminalApp.Osaleja objekt
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
     *
     * @param osalejad - antud massiv
     * @param osaleja  - antud Osaleja objekt
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

        // Koosta igale osalejale oma klass ja lisa osalejate massiivi.
        Osaleja[] osalejad = new Osaleja[0];
        int id = 0;
        for (String[] osalejadEdetabelis : osalejateNimed) {
            for (String osalejaNimi : osalejadEdetabelis) {
                // Kontrolli kas on juhendaja
                boolean juhendaja = false;
                if (osalejaNimi.contains("(Juh)")) {
                    juhendaja = true;
                    osalejaNimi = osalejaNimi.substring(0, osalejaNimi.length() - 6);
                }

                // Koosta osaleja objekt ja lisa osalejate massiivi.
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
            Edetabel edetabel = new Edetabel(i, edetabeliNimi, "ms");

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

        // Arvuta osalejate ELO ja koosta ELO edetabel
        Edetabel ELOedetabel = koostaELOEdetabel(edetabelid.length, osalejad);

        // Peamine loop
        boolean run = true;
        while (run) {
            Scanner sisend = new Scanner(System.in);
            System.out.print("[1] - vaata ülesande edetabelit\n[2] - ELO edetabel\n[3] - leia osaleja\n[x] - exit\n");
            String tekst = sisend.nextLine().toLowerCase();

            switch (tekst) {
                // edetabeli näitamine
                case "1":
                    System.out.println("Millist edetabelit soovite näha?");
                    int number = Math.max(1, Math.min(edetabelid.length, Integer.parseInt(String.valueOf(sisend.nextLine()))));
                    Edetabel edetabel = edetabelid[number - 1];
                    System.out.println(edetabel);
                    break;

                // ELO edetabel
                case "2":
                    System.out.println(ELOedetabel);
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

