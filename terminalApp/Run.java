package terminalApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Run {
    /**
     * Loeb faili read.
     * @param failiAsukoht - faili asukoht
     * @return - faili read massiivis
     */
    public static String[] loeFailiRead(String failiAsukoht) {
        StringBuilder vastus = new StringBuilder();
        File fail = new File(failiAsukoht);
        try {
            Scanner lugeja = new Scanner(fail, StandardCharsets.UTF_8);

            while (lugeja.hasNextLine()) {
                vastus.append(lugeja.nextLine()).append("\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return vastus.toString().split("\n");
    }

    /**
     * Kirjuta faili read
     * @param failiAsukoht - faili asukoht
     * @param read - read listis.
     */
    public static void kirjutaFaili(String failiAsukoht, List<String> read) {
        try {
            File fail = new File(failiAsukoht);
            FileWriter kirjutaja = new FileWriter(fail, false);

            for(String rida : read) {
                kirjutaja.write(rida);
                kirjutaja.write("\n");
            }

            kirjutaja.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        System.out.println("Kontrollin kas on uusim versioon.");

        // mida kontrollime
        String[] terminalAppCheck = {"Edetabel.java", "Kaabitseja.java", "Osaleja.java", "PHXC.java", "README.md"};
        String[] kaabitsejaCheck = {"Main.java", "README.md"};

        // listid
        List<String> terminalAppUuendustVajavad = new ArrayList<>();
        List<List<String>> terminalAppUuendustVajavadKood = new ArrayList<>();
        List<String> kaabitsejaUuendustVajavad = new ArrayList<>();
        List<List<String>> kaabitsejaUuendustVajavadKood = new ArrayList<>();

        for (String kood : terminalAppCheck) {
            HttpResponse<String> githubiKood = Kaabitseja.kaabitseLehekülge("https://raw.githubusercontent.com/im-byte/oop/main/terminalApp/" + kood);

            if (githubiKood != null) {
                String[] github = githubiKood.body().split("\n");
                String[] praegune = loeFailiRead("terminalApp/" + kood);

                for (int i = 0; i < github.length; i++) {
                    if (!github[i].stripTrailing().equals(praegune[i].stripTrailing())) {
                        terminalAppUuendustVajavad.add(kood);
                        terminalAppUuendustVajavadKood.add(List.of(github));
                        break;
                    }
                }
            }
        }
        for (String kood : kaabitsejaCheck) {
            HttpResponse<String> githubiKood = Kaabitseja.kaabitseLehekülge("https://raw.githubusercontent.com/im-byte/oop/main/kaabitseja/" + kood);

            if (githubiKood != null) {
                String[] github = githubiKood.body().split("\n");
                String[] praegune = loeFailiRead("kaabitseja/" + kood);

                for (int i = 0; i < github.length; i++) {
                    if (!github[i].stripTrailing().equals(praegune[i].stripTrailing())) {
                        kaabitsejaUuendustVajavad.add(kood);
                        kaabitsejaUuendustVajavadKood.add(List.of(github));
                        break;
                    }
                }
            }
        }


        if (!terminalAppUuendustVajavad.isEmpty() || !kaabitsejaUuendustVajavad.isEmpty()) {
            System.out.println("Github-is on uus versioon saadaval, kas soovite uuendada?");
            System.out.print("terminalApp-is muutuvad : ");
            System.out.println(terminalAppUuendustVajavad);
            System.out.print("kaabitsejas-is muutuvad : ");
            System.out.println(kaabitsejaUuendustVajavad);
            System.out.println("Tõmbame uuenduse? (y/n)");
            Scanner sisend = new Scanner(System.in);
            String tegevus = sisend.nextLine().toLowerCase();

            if (tegevus.equals("y")) {
                int i = 0;
                for(String fail : terminalAppUuendustVajavad) {
                    System.out.println("Uuendan : " + fail);
                    kirjutaFaili("terminalApp/" + fail, terminalAppUuendustVajavadKood.get(i));
                    i++;
                }
                int j = 0;
                for(String fail : kaabitsejaUuendustVajavad) {
                    System.out.println("Uuendan : " + fail);
                    kirjutaFaili("kaabitseja/" + fail, kaabitsejaUuendustVajavadKood.get(i));
                    j++;
                }
                System.out.println("Uuendus tehtud!");
                System.out.println();
            }
        } else {
            System.out.println("Praegu on uusim versioon!");
            System.out.println();
        }

        // jooksuta peamine programm.
        PHXC.main(args);
    }
}
