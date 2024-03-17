package terminalApp;
import java.net.http.HttpResponse;


public class Parser {
    /**
     * lisab massiivi* uue elemendi
     *
     * @param massiiv - antud massiiv
     * @param element - antud element
     * @return - tagastab "uue" massiivi, kuhu on lisatud antud elem.
     */
    public static String[] lisaMassiivi(String[] massiiv, String element) {
        String[] uus = new String[massiiv.length + 1];

        System.arraycopy(massiiv, 0, uus, 0, massiiv.length);
        uus[massiiv.length] = element;

        return uus;
    }

    /**
     * leiab pikast httpresponse tekstist välja edetabeli nimed.
     *
     * @param data - httpresponse
     * @return massiiv, kus on kõik edetabeli nimed.
     */
    public static String[] leiaEdetabeliNimed(HttpResponse<String> data) {
        // muuda pikk jada teksti massiiviks ridade põhjal.
        String[] dataArr = data.body().replace("\t", "").split("\n");
        String[] edetabeliNimed = new String[0];

        for (String line : dataArr) {
            if (line.startsWith("<h1>")) {
                edetabeliNimed = lisaMassiivi(edetabeliNimed, line.substring(4, line.length() - 5));
            }
        }

        return edetabeliNimed;
    }


    /**
     * leiab pikast httpresponse tekstist välja osalejad.
     *
     * @param data - httpresponse
     * @return massiiv, kus on kõik osalejad jaotamata.
     */
    public static String[][] leiaOsalejad(HttpResponse<String> data) {
        // muuda pikk jada teksti massiiviks ridade põhjal.
        String[] dataArr = data.body().replace("\t", "").split("\n");

        // leia mitu edetabelit on ja koosta kaherealine massiiv
        int edetabeleid = 0;
        for (String line : dataArr) {
            if (line.startsWith("<h1>")) {
                edetabeleid++;
            }
        }
        String[][] osalejad = new String[edetabeleid][0];

        // Lisa massiivi
        int e = -1;
        for (String line : dataArr) {
            if (line.startsWith("<h1>")) {
                e++;
            }

            if (line.startsWith("<span class=\"pseudo\">")) {
                String[] userData = line.substring(21, line.length() - 7).split("\\.");

                String osalejaNimi = userData[1].substring(1);
                if (userData[0].equals("Juh")){
                    osalejaNimi = userData[1].substring(1) + " (Juh)";
                }

                osalejad[e] = lisaMassiivi(osalejad[e], osalejaNimi);
            }
        }
        return osalejad;
    }

    /**
     * leiab pikast httpresponse tekstist välja tulemused.
     *
     * @param data - httpresponse
     * @return massiiv, kus on kõik tulemused jaotamata.
     */
    public static String[][] leiaTulemused(HttpResponse<String> data) {
        // muuda pikk jada teksti massiiviks ridade põhjal.
        String[] dataArr = data.body().replace("\t", "").split("\n");

        // leia mitu edetabelit on ja koosta kaherealine massiiv
        int edetabeleid = 0;
        for (String line : dataArr) {
            if (line.startsWith("<h1>")) {
                edetabeleid++;
            }
        }
        String[][] skoorid = new String[edetabeleid][0];

        // Lisa massiivi
        int e = -1;
        for (String line : dataArr) {
            if (line.startsWith("<h1>")) {
                e++;
            }

            if (line.startsWith("<span class=\"skoor\">")) {
                String skoor = line.substring(20, line.length() - 7)
                        .replace("<span class=\"komakoht\">", "")
                        .replace("</span>", "")
                        .split("ms")[0];
                skoorid[e] = lisaMassiivi(skoorid[e], skoor);
            }
        }
        return skoorid;
    }
}
