package terminalApp;
import java.net.http.HttpResponse;
import java.util.*;


public class PHXC {
    public static List<Osaleja> osalejad = new ArrayList<>();
    public static List<Edetabel> edetabelid = new ArrayList<>();

    /**
     * Kontrollib kas sõne on number või ei.
     *
     * @param str - antud sõne
     * @return - true/false põhinedes sellel kas sõne on number.
     */
    public static boolean kasOnNumber(String str) {
        if (str == null) return false;
        try {
            double a = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Leia osaleja nime järgi
     *
     * @param nimi - antud nimi
     * @return - osaleja objekt
     */
    public static Osaleja leiaOsaleja(String nimi) {
        for (Osaleja o : osalejad) {
            if (o.getNimi().equals(nimi)) return o;
        }
        return null;
    }

    /**
     * Koosta ELO edetabel
     *
     * @param osalejad - osalejate list.
     * @return - elo edetabel
     */
    public static Edetabel koostaELOEdetabel(List<Osaleja> osalejad) {
        float[][] sortimiseks = new float[osalejad.size()][2];
        Edetabel ELOedetabel = new Edetabel(999, "ELO", "p");

        int i = 0;
        for (Osaleja osaleja : osalejad) {
            osaleja.arvutaELO();

            sortimiseks[i][1] = osaleja.getELO();
            sortimiseks[i][0] = osaleja.getId();
            i++;
        }
        Arrays.sort(sortimiseks, Comparator.comparingDouble(a -> a[1]));

        List<String> osalejadNimed = new ArrayList<>();
        List<String> osalejadELO = new ArrayList<>();

        for (int j = sortimiseks.length - 1; j >= 0; j--) {
            float[] s = sortimiseks[j];
            int id = (int) s[0];

            for (Osaleja osaleja : osalejad) {
                if (osaleja.getId() == id) {
                    osalejadNimed.add(osaleja.getNimi());
                    osalejadELO.add(String.valueOf(osaleja.getELO()));
                }
            }
        }

        ELOedetabel.setTulemused(osalejadELO);
        ELOedetabel.setOsalejad(osalejadNimed);

        return ELOedetabel;
    }

    /**
     * Kaabitseb phxc.ee lehekülje andmed ja valmistab ette
     * osalejate objektid ning edetabelite objektid.
     */
    public static void PHXCInit() {
        HttpResponse<String> leheküljeAndmed = Kaabitseja.kaabitseLehekülge("http://www.phxc.ee");

        if (leheküljeAndmed != null) {
            List<List<List<String>>> andmed = Kaabitseja.LeiaEdetabeliteAndmed(leheküljeAndmed);

            // Edetabelid
            int edetabeleidKokku = andmed.get(0).size();

            for (int i = 0; i < edetabeleidKokku; i++) {
                String edetabeliNimi = andmed.get(0).get(i).getFirst();
                List<String> edetabeliOsalejad = andmed.get(1).get(i);
                List<String> edetabeliTulemused = andmed.get(2).get(i);
                List<String> edetabeliÜhikud = andmed.get(3).get(i);

                Edetabel edetabel = new Edetabel(i + 1, edetabeliNimi, edetabeliÜhikud.getFirst());
                edetabel.setOsalejad(edetabeliOsalejad);
                edetabel.setTulemused(edetabeliTulemused);

                edetabelid.add(edetabel);
            }

            // Osalejad
            int id = 0;
            for (int i = 0; i < edetabeleidKokku; i++) {
                List<String> edetabeliOsalejad = andmed.get(1).get(i);


                for (String o : edetabeliOsalejad) {
                    boolean juhendaja = o.contains("(Juh)");
                    String osalejaNimi = o.replace(" (Juh)", "");
                    Osaleja osaleja = leiaOsaleja(osalejaNimi);

                    if (osaleja == null) {
                        Osaleja osalejaUus = new Osaleja(id, osalejaNimi, juhendaja);
                        osalejaUus.lisaEdetabel(edetabelid.get(i));

                        osalejad.add(osalejaUus);
                        id++;
                    } else {
                        osaleja.lisaEdetabel(edetabelid.get(i));
                    }
                }
            }

            // ELO edetabel
            Edetabel elo = koostaELOEdetabel(osalejad);

            for(Osaleja osaleja : osalejad) {
                osaleja.setELOedetabel(elo);
            }

            edetabelid.addFirst(elo);
        }
    }


    public static void main(String[] args) {
        PHXCInit();

        boolean running = true;
        while (running) {
            Scanner sisend = new Scanner(System.in);
            System.out.print("[1] - Vaata ülesande edetabelit\n[2] - ELO edetabel\n[3] - Leia osaleja\n[x] - Exit\n");
            String tegevus = sisend.nextLine().toLowerCase();

            switch (tegevus) {
                // edetabeli näitamine
                case "1":
                    System.out.println("Millist edetabelit soovite näha? (id / nimi)");
                    String case1antud = sisend.nextLine();

                    Edetabel edetabel = null;

                    if (kasOnNumber(case1antud)) {
                        int number = Math.max(1, Math.min(edetabelid.size() - 1, Integer.parseInt(case1antud)));
                        edetabel = edetabelid.get(number);
                    } else {
                        for (Edetabel e : edetabelid) {
                            if (e.getNimi().equals(case1antud)) {
                                edetabel = e;
                                break;
                            }
                        }
                    }

                    if (edetabel != null) {
                        System.out.println(edetabel);
                    } else {
                        System.out.println("Sellise nimega edetabelit ei leitud.");
                    }
                    break;
                // näita ELO edetabel
                case "2":
                    System.out.println(edetabelid.getFirst());
                    break;
                // Leia osaleja nime pidi
                case "3":
                    System.out.print("Osaleja nimi: ");
                    String osalejaLeidmine = sisend.nextLine().toLowerCase();
                    System.out.println();

                    for (Osaleja osaleja : osalejad) {
                        if (osaleja.getNimi().toLowerCase().equals(osalejaLeidmine)) {
                            System.out.println(osaleja);
                            break;
                        }
                    }
                    break;
                case "x":
                    running = false;
                    break;
            }
        }

    }
}