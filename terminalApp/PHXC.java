package terminalApp;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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


    /**
     * Leia osaleja nime järgi
     *
     * @param nimi - antud nimi
     * @return - osaleja objekt
     */
    public static Osaleja leiaOsaleja(String nimi) {
        for(Osaleja osaleja : osalejad) {
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
            for(String nimi : osalejateNimed) {
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
            for(terminalApp.Edetabel edetabel : edetabelid) {
                for(String nimi : edetabel.getOsalejad()) {
                    Osaleja osaleja = leiaOsaleja(nimi.split(" ")[0]);

                    if (osaleja != null) osaleja.lisaEdetabel(edetabel);
                }
            }
        }
    }


    public static void main(String[] args) {
        // initialize.
        init();

        boolean running = true;
        while (running) {
            Scanner sisend = new Scanner(System.in);
            System.out.print("[1] - Vaata ülesande edetabelit\n[2] - Leia osaleja\n[x] - Exit\n");
            String tegevus = sisend.nextLine().toLowerCase();

            switch (tegevus) {
                case "1":
                    System.out.println("Millist edetabelit soovite näha? (id / nimi)");
                    String case1antud = sisend.nextLine();

                    terminalApp.Edetabel edetabel = null;

                    if (kasOnNumber(case1antud)) {
                        int number = Math.max(1, Math.min(edetabelid.size() - 1, Integer.parseInt(case1antud)));
                        edetabel = edetabelid.get(number);
                    } else {
                        for (terminalApp.Edetabel e : edetabelid) {
                            if (e.getNimi().equals(case1antud)) {
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
                case "2":
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