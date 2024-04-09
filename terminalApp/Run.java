package terminalApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Run {
    /**
     * Loeb faili read.
     *
     * @param failiAsukoht - faili asukoht
     * @return - faili read massiivis
     */
    public static List<byte[]> loeFailiRead(String failiAsukoht) {
        List<byte[]> vastus = new ArrayList<>();
        File fail = new File(failiAsukoht);
        try {
            Scanner lugeja = new Scanner(fail, StandardCharsets.UTF_8);

            while (lugeja.hasNextLine()) {
                byte[] rida = lugeja.nextLine().stripTrailing().getBytes(StandardCharsets.UTF_8);

                vastus.add(rida);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return vastus;
    }

    /**
     * Kirjuta faili read
     *
     * @param failiAsukoht - faili asukoht
     * @param read         - read listis.
     */
    public static void kirjutaFaili(String failiAsukoht, List<byte[]> read) {
        try {
            File fail = new File(failiAsukoht);
            FileWriter kirjutaja = new FileWriter(fail, false);

            for (byte[] rida : read) {
                kirjutaja.write(new String(rida));
                kirjutaja.write("\n");
            }

            kirjutaja.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        System.out.println("Kontrollime GitHub-ist, kas uuendusi on.");

        // mida kontrollime
        String[] toCheck = {"terminalApp/Edetabel.java", "terminalApp/Kaabitseja.java", "terminalApp/Osaleja.java", "terminalApp/PHXC.java", "terminalApp/README.md", "kaabitseja/README.md", "kaabitseja/Main.java"};
        List<String> muutaVaja = new ArrayList<>();
        List<List<byte[]>> uusKood = new ArrayList<>();

        for (String fail : toCheck) {
            // Loeme github-i koodi
            HttpResponse<String> githubiKood = Kaabitseja.kaabitseLehekülge("https://raw.githubusercontent.com/im-byte/oop/main/" + fail);
            List<byte[]> githubiKoodBytes = new ArrayList<>();
            for (String rida : githubiKood.body().split("\n")) {
                githubiKoodBytes.add(rida.stripTrailing().getBytes(StandardCharsets.UTF_8));
            }

            // Loeme lokaalse koodi
            List<byte[]> praeguneKoodBytes = loeFailiRead(fail);

            // Võrdleme
            for (int i = 0; i < Math.min(praeguneKoodBytes.size(), githubiKoodBytes.size()); i++) {
                if (!Arrays.equals(praeguneKoodBytes.get(i), githubiKoodBytes.get(i))) {
                    muutaVaja.add(fail);
                    uusKood.add(githubiKoodBytes);
                    break;
                }
            }
        }

        if (!muutaVaja.isEmpty()) {
            System.out.println("--------------------------------");
            System.out.println("Uus versioon saadaval GitHub-is.");
            System.out.println();
            System.out.println("Failid, mida muudetakse:");

            for(String m : muutaVaja) {
                System.out.println("- " + m);
            }
            System.out.println();
            System.out.println("Kas uuendame? (y/n)");
            System.out.print("Tegevus: ");
            Scanner sisend = new Scanner(System.in);
            String tegevus = sisend.nextLine().toLowerCase();

            if (tegevus.equals("y")) {
                System.out.println();
                int i = 0;
                for(String fail : muutaVaja) {
                    System.out.println("Uuendan: " + fail);
                    kirjutaFaili(fail, uusKood.get(i));
                    i++;
                }
                System.out.println("\nUuendatud!");
            }
            System.out.println("--------------------------------");
            System.out.println();
        } else {
            System.out.println("Uuendusi pole.");
            System.out.println();
        }


        // jooksuta peamine programm.
        PHXC.main(args);
    }
}
