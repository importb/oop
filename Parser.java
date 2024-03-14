import java.net.http.HttpResponse;
import java.util.ArrayList;

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
     * leiab pikast httpresponse tekstist välja osalejad.
     *
     * @param data - httpresponse
     * @return massiiv, kus on kõik osalejad jaotamata.
     */
    public static String[] leiaOsalejad(HttpResponse<String> data) {
        // muuda pikk jada teksti massiiviks ridade põhjal.
        String[] dataArr = data.body().replace("\t", "").split("\n");

        // leia kasutajad ja lisa massiivi
        String[] osalejad = new String[0];

        for (String line : dataArr) {
            if (line.startsWith("<span class=\"pseudo\">")) {
                String user = line.substring(21, line.length() - 7);
                osalejad = lisaMassiivi(osalejad, user);
            }
        }
        return osalejad;
    }


    public static String[] leiaTulemused(HttpResponse<String> data) {
        // muuda pikk jada teksti massiiviks ridade põhjal.
        String[] dataArr = data.body().replace("\t", "").split("\n");

        // leia kasutajad ja lisa massiivi
        String[] tulemused = new String[0];

        for (String line : dataArr) {
            if (line.startsWith("<span class=\"skoor\">")) {
                String tulemus = line.substring(20, line.length() - 7)
                        .replace("<span class=\"komakoht\">", "")
                        .replace("</span>", "");
                tulemused = lisaMassiivi(tulemused, tulemus);
            }
        }
        return tulemused;
    }

    public static String[] leiaEdetabel(String[] osalejad, String[] tulemused, int edetabeliIndeks) {
        int eIndeks = 0;  // edetabeli indeks.
        String[] edetabel = new String[0];

        int indeks = 0;
        for (String osaleja : osalejad) {
            // Leia koht numbrina
            String koht = osaleja.split("\\.")[0];
            int kohaNr = 999;  // juhendaja koht. todo. maybe?
            if (!koht.equals("Juh")) {
                kohaNr = Integer.parseInt(koht);
            }

            // edetabeli indeks
            if (kohaNr == 1) eIndeks++;

            // lisa edetabelisse
            if ((eIndeks == edetabeliIndeks) && (kohaNr != 999)){
                edetabel = lisaMassiivi(edetabel, String.format("%s - %s", osaleja, tulemused[indeks]));
            }

            indeks++;
        }

        return edetabel;
    }

}
