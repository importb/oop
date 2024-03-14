import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // scrape website
            HttpResponse<String> unparsedData = Scraper.scrapeWebsite("http://www.phxc.ee");

            // loeme osalejad ja tulemused mällu.
            // nb! ei ole sorditud edetabelite järgi, kõik edetabelid on koos.
            // edetabeli osalejate ja tulemuste leidmise jaoks on Parser.leiaEdetabel
            String[] osalejad = Parser.leiaOsalejad(unparsedData);
            String[] tulemused = Parser.leiaTulemused(unparsedData);

            boolean run = true;
            while (run) {
                Scanner sisend = new Scanner(System.in);
                System.out.print("Tegevus? [x] - quit, [o] - osalejad, [t] - tulemused, [e] - edetabel: ");
                String tekst = sisend.nextLine().toLowerCase();

                switch (tekst) {
                    case "x":
                        run = false;
                        break;
                    case "o":
                        System.out.println();
                        System.out.println(Arrays.toString(osalejad));
                        System.out.println();
                        break;
                    case "t":
                        System.out.println();
                        System.out.println(Arrays.toString(tulemused));
                        System.out.println();
                        break;
                    case "e":
                        System.out.print("Mitmendat edetabelit näidata?: ");
                        int eIndeks = Integer.parseInt(sisend.nextLine());
                        String[][] edetabelid = Parser.leiaEdetabel(osalejad, tulemused, eIndeks);
                        String[] edetabeliOsalejad = edetabelid[0];
                        String[] edetabeliTulemused = edetabelid[1];

                        System.out.println();
                        System.out.println("Osalejad:");
                        System.out.println(Arrays.toString(edetabeliOsalejad));
                        System.out.println("Tulemused:");
                        System.out.println(Arrays.toString(edetabeliTulemused));
                        System.out.println();
                        break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
