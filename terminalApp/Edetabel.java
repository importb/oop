package terminalApp;

import java.util.List;

public class Edetabel {
    private final String nimi;
    private final int id;
    private final String skooriÜhik;
    private List<String> osalejad;
    private List<String> tulemused;

    public Edetabel(int id, String nimi, String skooriÜhik) {
        this.id = id;
        this.nimi = nimi;
        this.skooriÜhik = skooriÜhik;
    }

    public void setOsalejad(List<String> osalejad) {
        this.osalejad = osalejad;
    }

    public void setTulemused(List<String> tulemused) {
        this.tulemused = tulemused;
    }

    public String getNimi() {
        return nimi;
    }

    public String getSkooriÜhik() {
        return skooriÜhik;
    }

    public List<String> getOsalejad() {
        return osalejad;
    }


    /**
     * Leiab osaleja objekti nime kaudu
     * @param nimi - antud nimi
     * @return - osaleja objekt
     */
    public int leiaOsalejaKoht(String nimi) {
        int i = 0;
        for (String osaleja : osalejad) {
            if (osaleja.split(" ")[0].equals(nimi)) {
                break;
            }
            i++;
        }

        return i;
    }

    /**
     * Leiab osaleja objekti nime kaudu (ilma juhendajadeta)
     * @param nimi - antud nimi
     * @return - osaleja objekt
     */
    public int leiaOsalejaKohtIlmaJuh(String nimi) {
        int i = 0;
        for (String osaleja : osalejad) {
            if (osaleja.contains("(Juh)")) continue;

            if (osaleja.equals(nimi)) {
                break;
            }
            i++;
        }

        return i;
    }

    /**
     * Leiab osaleja objekti nime kaudu (ainult juhendajad)
     * @param nimi - antud nimi
     * @return - osaleja objekt
     */
    public int leiaOsalejaKohtJuh(String nimi) {
        int i = 0;
        for (String osaleja : osalejad) {

            if (osaleja.endsWith("(Juh)")){
                if (osaleja.contains(nimi)) break;
                i++;
            }
        }
        return i;
    }

    public String leiaOsalejaTulemus(String nimi) {
        int i = leiaOsalejaKoht(nimi);

        return tulemused.get(i);
    }

    public String leiaOsalejaTulemus(int koht) {
        return tulemused.get(koht);
    }


    public String toString() {
        StringBuilder info = new StringBuilder();

        for (int i = 0; i < osalejad.size(); i++) {
            String tühikuid = " ".repeat(30 - ((i + 1) + osalejad.get(i)).length());
            info.append(String.format("%s. %s%s%s%s\n", i + 1, osalejad.get(i), tühikuid, tulemused.get(i), skooriÜhik));
        }

        return String.format(
                """
                        [ %s ] [id : %s]
                        %s
                        """,
                nimi, id, info);
    }
}
