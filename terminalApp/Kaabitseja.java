package terminalApp;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Kaabitseja {
    public static HttpResponse<String> kaabitseLehekülge(String link) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(link)).build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {
        }

        return null;
    }

    public static terminalApp.Edetabel leiaEdetabel(HttpResponse<String> data, int id) {
        String[] body = data.body().replace("\t", "").split("\n");

        // edetabel mille tagastame
        terminalApp.Edetabel edetabel = new terminalApp.Edetabel(id);

        int praeguneEdetabel = 0;
        List<List<String>> andmed = new ArrayList<>();
        List<String> osalejaAndmed = new ArrayList<>();
        for (String line : body) {
            // leiame otsitava edetabeli
            if (line.contains("<h1>")) praeguneEdetabel++;
            if (praeguneEdetabel != id) continue;

            // Edetabeli nimi
            if (line.contains("<h1>")) edetabel.setNimi(line.substring(4, line.length() - 5));

            // leiame osaleja nime
            if (line.contains("pseudo")) {
                String[] osaleja = line.replace("</span>", "")
                        .replace("<span class=\"pseudo\">", "")
                        .replace(" ", "")
                        .replace("&amp;", "&")
                        .split("\\.");

                String osalejaNimi = osaleja[1];

                // Vaata kas juhendaja.
                if (osaleja[0].equals("Juh")) osalejaNimi += " (Juh)";

                osalejaAndmed.add(osalejaNimi);
            }

            // leiame osaleja skoori
            if (line.contains("skoor\"")) {
                String skoor = line.substring(20, line.length() - 7)
                        .replace("<span class=\"komakoht\">", "")
                        .replace("</span>", "");

                osalejaAndmed.add(skoor);
            }

            // uus osaleja
            if (line.equals("</div>")) {
                if (!osalejaAndmed.isEmpty()) andmed.add(osalejaAndmed);
                osalejaAndmed = new ArrayList<>();
            }
        }

        // leiame osalejate nime ja skoorid.
        List<String> osalejadFinal = new ArrayList<>();
        List<List<String>> tulemusedFinal = new ArrayList<>();
        List<List<String>> skooriühikudFinal = new ArrayList<>();

        for (List<String> osaleja : andmed) {
            // lisame nime
            osalejadFinal.add(osaleja.getFirst());

            // lisame tulemused ja skooriühikud
            List<String> tulemused = new ArrayList<>();
            List<String> skooriÜhikud = new ArrayList<>();

            for (int i = 1; i < osaleja.size(); i++) {
                String[] skooriAndmed = osaleja.get(i).split(" ");

                // Skoor ise
                if (skooriAndmed.length > 0) {
                    tulemused.add(skooriAndmed[0]);
                } else {
                    tulemused.add("");
                }

                // Skooriühik
                if (skooriAndmed.length > 1) {
                    skooriÜhikud.add(skooriAndmed[1]);
                } else {
                    skooriÜhikud.add("");
                }

            }

            tulemusedFinal.add(tulemused);
            skooriühikudFinal.add(skooriÜhikud);
        }


        // Seadista edetabel
        edetabel.setOsalejad(osalejadFinal);
        edetabel.setTulemused(tulemusedFinal);
        edetabel.setSkooriÜhikud(skooriühikudFinal);

        return edetabel;
    }

    public static List<String> leiaOsalejad(HttpResponse<String> data) {
        String[] body = data.body().replace("\t", "").split("\n");
        Set<String> osalejateNimedSet = new HashSet<>();

        for (String line : body) {
            if (line.contains("pseudo")) {
                String[] osaleja = line.substring(21, line.length() - 7).split("\\.");

                String nimi = osaleja[1].trim();
                if (osaleja[0].equals("Juh")) nimi += " [JUH]";

                osalejateNimedSet.add(nimi);
            }
        }

        return new ArrayList<>(osalejateNimedSet);
    }
}


