import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // yoink data
            HttpResponse<String> unparsedData = Scraper.scrapeWebsite("http://www.phxc.ee");
            // parse data?
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
                        String[] edetabel = Parser.leiaEdetabel(osalejad, tulemused, eIndeks);
                        System.out.println();
                        System.out.println(Arrays.toString(edetabel));
                        System.out.println();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}