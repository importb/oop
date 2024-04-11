package terminalApp;

import java.net.http.HttpResponse;
import java.util.*;

public class PHXC {
    static List<terminalApp.Edetabel> edetabelid = new ArrayList<>();
    static List<Osaleja> osalejad = new ArrayList<>();

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

    public static void koostaELOEdetabel(List<Osaleja> osalejad) {
        float[][] sortimiseks = new float[osalejad.size()][2];
        List<List<String>> skooriühikud = new ArrayList<>();
        Edetabel ELOedetabel = new Edetabel(999);
        ELOedetabel.setNimi("ELO");

        int i = 0;
        for (Osaleja osaleja : osalejad) {
            skooriühikud.add(Collections.singletonList("p"));

            osaleja.arvutaELO();

            sortimiseks[i][1] = osaleja.arvutaELO();
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
                    String nimi = osaleja.getNimi();

                    if (osaleja.isJuhendaja()) nimi += " (Juh)";

                    osalejadNimed.add(nimi);
                    osalejadELO.add(String.valueOf(osaleja.arvutaELO()));
                }
            }
        }

        // Tulemused viimane list.
        List<List<String>> tulemused = new ArrayList<>();
        for (String ELO : osalejadELO) {
            tulemused.add(Collections.singletonList(ELO));
        }

        // lisa edetabelisse.
        ELOedetabel.setTulemused(tulemused);
        ELOedetabel.setOsalejad(osalejadNimed);
        ELOedetabel.setSkooriÜhikud(skooriühikud);

        // Lisa osalejatele
        for (Osaleja osaleja : osalejad) osaleja.lisaELOEdetabel(ELOedetabel);

        // lisa edetabelid listi
        edetabelid.addFirst(ELOedetabel);
    }

    /**
     * Leia osaleja nime järgi
     *
     * @param nimi - antud nimi
     * @return - osaleja objekt
     */
    public static Osaleja leiaOsaleja(String nimi) {
        for (Osaleja osaleja : osalejad) {
            if (osaleja.getNimi().equals(nimi)) {
                return osaleja;
            }
        }
        return null;
    }


    /**
     * Kaabitseb phxc.ee lehekülje andmed ja valmistab ette
     * osalejate objektid ning edetabelite objektid.
     */
    public static void init() {
        // Leia lehekülje andmed.
        HttpResponse<String> leheküljeAndmed = Kaabitseja.kaabitseLehekülge("https://www.phxc.ee");
        if (leheküljeAndmed == null) leheküljeAndmed = Kaabitseja.kaabitseLehekülge("http://www.phxc.ee");

        // init
        if (leheküljeAndmed != null) {
            // Edetabelid
            for (int i = 0; i < 15; i++) {
                terminalApp.Edetabel edetabel = Kaabitseja.leiaEdetabel(leheküljeAndmed, i);

                if (edetabel.getNimi() != null) {
                    edetabelid.add(edetabel);
                }
            }

            // Osalejad
            List<String> osalejateNimed = Kaabitseja.leiaOsalejad(leheküljeAndmed);

            int id = 0;
            for (String nimi : osalejateNimed) {
                boolean juhendaja = false;
                if (nimi.contains("[JUH]")) {
                    juhendaja = true;
                    nimi = nimi.substring(0, nimi.length() - 6);
                }

                Osaleja osaleja = new Osaleja(id, nimi, juhendaja);
                osalejad.add(osaleja);
                id++;
            }

            // Lisa osalejatele edetabelid.
            for (terminalApp.Edetabel edetabel : edetabelid) {
                for (String nimi : edetabel.getOsalejad()) {
                    Osaleja osaleja = leiaOsaleja(nimi.split(" ")[0]);

                    if (osaleja != null) osaleja.lisaEdetabel(edetabel);
                }
            }
        }

        // ELO edetabel
        koostaELOEdetabel(osalejad);
    }


    public static void main(String[] args) {
        // initialize.
        init();
        int praeguneEdetabel = 1;

        boolean running = true;
        while (running) {
            Scanner sisend = new Scanner(System.in);
            System.out.print("[1] - Vaata ülesannete edetabeleid\n[2] - Edetabeli otsing\n[3] - Osaleja otsing\n[4] - ELO edetabel\n[5] - Statistika\n[x] - Exit\n");
            String tegevus = sisend.nextLine().toLowerCase();

            switch (tegevus) {
                case "1":
                    while (true) {
                        System.out.println(edetabelid.get(praeguneEdetabel));
                        System.out.println("[v] - Järgmine");
                        System.out.println("[c] - Eelmine");
                        System.out.println("[x] - Tagasi");
                        String tegevus1 = sisend.nextLine();

                        if (tegevus1.equals("x")) {
                            praeguneEdetabel = 1;
                            break;
                        }
                        if (tegevus1.equals("c")) praeguneEdetabel = Math.max(1, praeguneEdetabel - 1);
                        if (tegevus1.equals("v"))
                            praeguneEdetabel = Math.min(edetabelid.size() - 1, praeguneEdetabel + 1);
                    }
                    break;
                case "2":
                    System.out.println("Millist edetabelit soovite näha? (id / nimi)");
                    String tegevus2 = sisend.nextLine();

                    terminalApp.Edetabel edetabel = null;

                    if (kasOnNumber(tegevus2)) {
                        int number = Math.max(1, Math.min(edetabelid.size() - 1, Integer.parseInt(tegevus2)));
                        edetabel = edetabelid.get(number);
                    } else {
                        for (terminalApp.Edetabel e : edetabelid) {
                            if (e.getNimi().equals(tegevus2)) {
                                edetabel = e;
                                break;
                            }
                        }
                    }

                    if (edetabel != null) {
                        System.out.println();
                        System.out.println(edetabel);
                    } else {
                        System.out.println("Sellise nimega edetabelit ei leitud.");
                    }
                    break;
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
                case "4":
                    System.out.println(edetabelid.getFirst());
                    break;
                case "5":
                    // Leiame kes on kõige rohkem ülesandeid lahendanud.
                    List<String> nimed = new ArrayList<>();
                    int suurim = Integer.MIN_VALUE;

                    for (Osaleja o : osalejad) {
                        int kogus = o.getEdetabelid().size();

                        if (kogus == suurim) nimed.add(o.getNimi());
                        if (kogus > suurim) {
                            suurim = kogus;
                            nimed = new ArrayList<>();
                            nimed.add(o.getNimi());
                        }
                    }

                    // parim %
                    float parimProtsent = 0;
                    String parimProtsentOsaleja = "";
                    String parimProtsentEdetabel = "";
                    for (Edetabel e : edetabelid) {
                        float skoor1 = Float.parseFloat(e.leiaTulemus(0).getFirst());
                        float skoor2 = Float.parseFloat(e.leiaTulemus(1).getFirst());
                        float vahe = (skoor2 / skoor1) * 100;

                        if (vahe > parimProtsent) {
                            parimProtsent = vahe;
                            parimProtsentOsaleja = e.getOsalejad().getFirst();
                            parimProtsentEdetabel = e.getNimi();
                        }
                    }

                    // parim keskmine koht
                    float parimKeskmineKoht = Float.MAX_VALUE;
                    String parimKeskmineNimi = "";
                    for(Osaleja o : osalejad) {
                        int koht = 0;
                        int kohtiKokku = 0;

                        for(Edetabel e : edetabelid) {
                            int eKoht = e.leiaKoht(o);

                            if (eKoht != -1) {
                                koht += eKoht;
                                kohtiKokku++;
                            }
                        }

                        float keskmine = (float) koht / kohtiKokku;

                        if (keskmine < parimKeskmineKoht) {
                            parimKeskmineKoht = keskmine;
                            parimKeskmineNimi = o.getNimi();
                        }
                    }

                    System.out.printf("""
                                    [ Statistika ]
                                    - Osalejaid kokku:
                                      %s
                                      
                                    - Ülesandeid kokku:
                                      %s
                                     
                                    - Kõige rohkem ülesandeid lahendanud. (Lahendanud: %s)
                                      %s
                                      
                                    - Kõige parema saavutuse saanud %% poolest.
                                      %s, Edetabel: %s, Protsent: %s%%
                                      
                                    - Kõige parema keskmise kohaga osaleja:
                                      %s, keskmine koht: %.4f
                                    
                                    - ELO edetabeli 1. koht.
                                      %s (%s p)
                                    %n""",
                            osalejad.size(),
                            edetabelid.size() - 1,
                            suurim,
                            nimed,
                            parimProtsentOsaleja,
                            parimProtsentEdetabel,
                            parimProtsent,
                            parimKeskmineNimi,
                            parimKeskmineKoht,
                            edetabelid.getFirst().getOsalejad().getFirst(),
                            edetabelid.getFirst().leiaTulemus(0).getFirst()
                    );
                    break;
                case "x":
                    running = false;
                    break;
            }
        }
    }
}
