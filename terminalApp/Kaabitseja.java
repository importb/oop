package terminalApp;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kaabitseja {
    public static HttpResponse<String> kaabitseLehekülge(String link) {
        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(link)).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {}

        return null;
    }

    public static List<List<List<String>>> LeiaEdetabeliteAndmed(HttpResponse<String> data) {
        String[] dataArr = data.body().replace("\t", "").split("\n");

        List<List<List<String>>> andmed = new ArrayList<>();

        // Nendest listidest koostatakse viimane return List.
        List<String> edetabeliteNimed = new ArrayList<>();
        List<List<String>> edetabeliteOsalejad = new ArrayList<>();
        List<List<String>> edetabeliteTulemused = new ArrayList<>();
        List<List<String>> edetabeliteÜhikud = new ArrayList<>();

        // Need listid tühjendatakse igakord, kui uus edetabel tuleb.
        List<String> edetabeliOsalejad = new ArrayList<>();
        List<String> edetabeliTulemused = new ArrayList<>();
        List<String> edetabeliÜhikud = new ArrayList<>();

        for (String line : dataArr) {
            // Järgmine edetabel
            if (line.startsWith("<h1>")) {
                if (!edetabeliteNimed.isEmpty()) {
                    // Osalejad
                    edetabeliteOsalejad.add(edetabeliOsalejad);
                    edetabeliOsalejad = new ArrayList<>();

                    // Tulemused
                    edetabeliteTulemused.add(edetabeliTulemused);
                    edetabeliTulemused = new ArrayList<>();

                    // Ühikud
                    edetabeliteÜhikud.add(edetabeliÜhikud);
                    edetabeliÜhikud = new ArrayList<>();
                }

                // Edetabeli nimi
                edetabeliteNimed.add(line.substring(4, line.length()-5));
            }


            // Edetabeli osalejad
            if (line.startsWith("<span class=\"pseudo\">")) {
                String[] osaleja = line
                        .replace("</span>", "")
                        .replace("<span class=\"pseudo\">", "")
                        .replace(" ", "")
                        .replace("&amp;", "&")
                        .split("\\.");

                String osalejaNimi = osaleja[1];

                if (osaleja[0].equals("Juh")) {
                    osalejaNimi += " (Juh)";
                }

                edetabeliOsalejad.add(osalejaNimi);
            }

            // Edetabeli tulemused
            if (line.startsWith("<span class=\"skoor\">")) {
                String[] skoor = line.substring(20, line.length() - 7)
                        .replace("<span class=\"komakoht\">", "")
                        .replace("</span>", "").split(" ");

                // Leia tulemuse ühik
                String ühik = "";
                if (skoor.length > 1){
                    ühik = skoor[1];
                }

                edetabeliÜhikud.add(ühik);
                edetabeliTulemused.add(skoor[0]);
            }
        }

        // Lisa viimased ka
        edetabeliteOsalejad.add(edetabeliOsalejad);
        edetabeliteTulemused.add(edetabeliTulemused);
        edetabeliteÜhikud.add(edetabeliÜhikud);

        // Lisa lõpp tulemusse.
        List<List<String>> eNimed = new ArrayList<>();

        for(String n : edetabeliteNimed) {
            eNimed.add(Collections.singletonList(n));
        }

        andmed.add(eNimed);
        andmed.add(edetabeliteOsalejad);
        andmed.add(edetabeliteTulemused);
        andmed.add(edetabeliteÜhikud);

        return andmed;
    }
}
