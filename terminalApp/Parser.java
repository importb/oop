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
                edetabeliNimed = lisaMassiivi(edetabeliNimed, line.substring(4, line.length()-5));
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

    /**
     * leiab pikast httpresponse tekstist välja tulemused.
     *
     * @param data - httpresponse
     * @return massiiv, kus on kõik tulemused jaotamata.
     */
    public static String[] leiaTulemused(HttpResponse<String> data) {
        // muuda pikk jada teksti massiiviks ridade põhjal.
        String[] dataArr = data.body().replace("\t", "").split("\n");

        // leia tulemused ja lisa massiivi
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

    /**
     * tagastab otsitava edetabeli osalejad ja nende tulemused.
     * @param osalejad - antud osalejate massiiv
     * @param tulemused - antud tulemuste massiiv
     * @param edetabeliIndeks - mitmendat edetabelit otsime.
     * @return - kahejärjendiline massiiv, kus 0. on osalejate massiiv ja 1. on tulemuste massiiv.
     */
    public static String[][] leiaEdetabel(String[] osalejad, String[] tulemused, int edetabeliIndeks) {
        String[][] tagastus = new String[2][];
        String[] osalejateEdetabel = new String[0];
        String[] tulemusteEdetabel = new String[0];
        int praeguneEdetabel = 0;

        for (int i = 0; i < osalejad.length; i++) {
            // Osaleja info
            String osaleja = osalejad[i];
            String osalejaKohtStr = osaleja.split("\\.")[0];

            if (!osalejaKohtStr.equals("Juh")) {
                // osaleja koht int
                int osalejaKoht = Integer.parseInt(osalejaKohtStr);

                // Tulemuse info
                String tulemus = tulemused[i];

                // Vaatame, kas oleme jõudnud uue edetabelini
                if (osalejaKoht == 1) praeguneEdetabel++;

                // Lisame massiividesse
                if (edetabeliIndeks == praeguneEdetabel) {
                    osalejateEdetabel = lisaMassiivi(osalejateEdetabel, osaleja);
                    tulemusteEdetabel = lisaMassiivi(tulemusteEdetabel, tulemus);
                }
            }
        }

        // Lisame tagastusmassiivi
        tagastus[0] = osalejateEdetabel;
        tagastus[1] = tulemusteEdetabel;

        return tagastus;
    }

}
