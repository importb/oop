import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {


    public static boolean jubaMassiivis(Osaleja[] massiiv, Osaleja osaleja){
        for(Osaleja o : massiiv){
            if (o.getNimi().equals(osaleja.getNimi())) {
                return true;
            }
        }
        return false;
    }
    public static Osaleja[] lisaMassiivi(Osaleja[] massiiv, Osaleja osaleja){
        Osaleja[] uus = new Osaleja[massiiv.length + 1];
        System.arraycopy(massiiv, 0, uus, 0, massiiv.length);
        uus[uus.length - 1] = osaleja;
        return uus;
    }

    public static void main(String[] args) throws Exception {
        // scrape website
        HttpResponse<String> unparsedData = Scraper.scrapeWebsite("http://www.phxc.ee");


        // Massiivid
        String[][] osalejateNimed = Parser.leiaOsalejad(unparsedData);
        String[][] osalejateSkoorid = Parser.leiaTulemused(unparsedData);
        String[] edetabeliNimed = Parser.leiaEdetabeliNimed(unparsedData);
        Edetabel[] edetabelid = new Edetabel[edetabeliNimed.length];

        for(int i = 0; i < edetabeliNimed.length; i++){
            Edetabel edetabel = new Edetabel(i, edetabeliNimed[i]);

            // Lisa osaleja
            for(String oNimi : osalejateNimed[i]){
                Osaleja osaleja = new Osaleja(oNimi);
                edetabel.lisaOsaleja(osaleja);
            }

            // Lisa skoor
            for(String oSkoor : osalejateSkoorid[i]){
                edetabel.lisaSkoor(Float.parseFloat(oSkoor));
            }

            edetabelid[i] = edetabel;
        }

        boolean run = true;
        while (run) {
            Scanner sisend = new Scanner(System.in);
            System.out.print("[1] - vaata ülesande edetabelit\n[2] - leia osaleja\n[x] - exit\n");
            String tekst = sisend.nextLine().toLowerCase();

            switch (tekst) {
                // edetabeli näitamine
                case "1":
                    System.out.println("Millist edetabelit soovite näha?");
                    int number = Integer.parseInt(String.valueOf(sisend.nextLine()));


                    Edetabel edetabel = edetabelid[number - 1];

                    System.out.println(edetabel);

                    break;

                // osaleja otsimine
                case "2":
                    System.out.println("Keda otsite?");
                    String nimi = String.valueOf(sisend.nextLine());
                    Osaleja otsitav = null;

                    for(Edetabel e : edetabelid){
                        Osaleja osaleja = e.leiaOsaleja(nimi);

                        if (osaleja != null){
                            otsitav = osaleja;
                            break;
                        }
                    }

                    if (otsitav != null){
                        System.out.println(otsitav.getNimi());
                    }else{
                        System.out.println("Ei leidnud sellise nimega osalejat.");
                    }

                    break;

                // exit.
                case "x":
                    run = false;
                    break;
            }
        }
    }
}

