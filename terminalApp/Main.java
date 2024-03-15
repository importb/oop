package terminalApp;
import java.net.http.HttpResponse;
import java.util.Scanner;

// import Parser;
// import Scraper;

public class Main {
    public static void main(String[] args) {
        try {
            // scrape website
            HttpResponse<String> unparsedData = Scraper.scrapeWebsite("http://www.phxc.ee");

            // Massiivid
            String[] osalejad = Parser.leiaOsalejad(unparsedData);
            String[] tulemused = Parser.leiaTulemused(unparsedData);
            String[] edetabeliNimed = Parser.leiaEdetabeliNimed(unparsedData);

            boolean run = true;
            while (run) {
                Scanner sisend = new Scanner(System.in);
                System.out.print("[1] - vaata ülesande edetabelit\n[x] - exit\n");
                String tekst = sisend.nextLine().toLowerCase();

                switch (tekst) {
                    case "x":
                        run = false;
                        break;
                    case "1":
                        // Uurime mis edetabelit vaadata.
                        System.out.print("Millist edetabelit soovite näha?: ");
                        int edetabeli_id = Integer.parseInt(sisend.nextLine());
                        String[][] edetabeliInfo = Parser.leiaEdetabel(osalejad, tulemused, edetabeli_id);

                        // edetabeli nimi
                        System.out.println();
                        System.out.println("[ " + edetabeliNimed[edetabeli_id - 1].toUpperCase() + " ]");

                        // edetabeli osalejad ja nende skoor.
                        String[] edetabeliOsalejad = edetabeliInfo[0];
                        String[] edetabeliSkoorid = edetabeliInfo[1];

                        for (int i = 0; i < edetabeliOsalejad.length; i++) {
                            String osaleja = edetabeliOsalejad[i];
                            String skoor = edetabeliSkoorid[i];
                            String spacing = " ".repeat(30 - osaleja.length());
                            System.out.printf("%s%s%s%n", osaleja, spacing, skoor);
                        }
                        System.out.println();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
