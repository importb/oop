package terminalApp;

import java.util.ArrayList;
import java.util.List;

public class Osaleja {
    private final int id;
    private final String nimi;
    private final boolean juhendaja;
    private float ELO;
    private Edetabel ELOedetabel;
    private List<Edetabel> edetabelid = new ArrayList<>();

    public Osaleja(int id, String nimi, boolean juhendaja) {
        this.id = id;
        this.nimi = nimi;
        this.juhendaja = juhendaja;
    }

    public void setELOedetabel(Edetabel uus) {
        this.ELOedetabel = uus;
    }

    public String getNimi() {
        return nimi;
    }

    public float getELO() {
        return ELO;
    }

    public int getId() {
        return id;
    }

    public static double easeInCubic(double x) {
        return x * x * x;
    }

    /**
     * Arvutab osaleja ELO põhinedes tema kohale edetabelites.
     * ELO saamine edetabeli kohta oleneb osaleja kohast edetabelis ning palju tulemusi edetabelis on.
     * Valisin EaseInCubic, kuna see tundub kõige parem tasakaal koha ning mitmes edetabelis
     * osalenud on vahel.
     */
    public void arvutaELO() {
        float uusELO = 100;

        for (Edetabel edetabel : edetabelid) {
            int koht = edetabel.leiaOsalejaKoht(nimi);
            int kohtiKokku = edetabel.getOsalejad().size();

            uusELO += (float) easeInCubic(0.8 - ((double) koht / kohtiKokku)) * 45;
        }

        this.ELO = Math.max(0, uusELO);
    }

    public void lisaEdetabel(Edetabel edetabel) {
        edetabelid.add(edetabel);
    }

    public String toString() {
        StringBuilder edetabeliteNimed = new StringBuilder();
        StringBuilder edetabeliteTulemused = new StringBuilder();
        StringBuilder edetabeliteKohad = new StringBuilder();
        StringBuilder edetabeliteKohadEri = new StringBuilder();
        String eri = "ilma juh";

        for (Edetabel edetabel : edetabelid) {
            edetabeliteNimed.append("  [ ").append(edetabel.getNimi()).append(" ] \n");

            String tulemus = edetabel.leiaOsalejaTulemus(this.getNimi());
            String parimTulemus = edetabel.leiaOsalejaTulemus(0);
            String ühik = edetabel.getSkooriÜhik();

            double paremProtsent = Double.parseDouble(tulemus) / Double.parseDouble(parimTulemus) * 100;
            edetabeliteTulemused.append(
                    String.format("  %s%s(Parim: %s, %.0f%% parem)\n",
                            tulemus + ühik,
                            " ".repeat(10 - (tulemus + ühik).length()),
                            parimTulemus + ühik,
                            paremProtsent)
            );


            edetabeliteKohad
                    .append("[ ")
                    .append(edetabel.leiaOsalejaKoht(this.getNimi()) + 1)
                    .append(". ] ");

            if (!juhendaja){
                edetabeliteKohadEri
                        .append("[ ")
                        .append(edetabel.leiaOsalejaKohtIlmaJuh(this.getNimi()) + 1)
                        .append(". ] ");
            }else{
                eri = "juh";
                edetabeliteKohadEri
                        .append("[ ")
                        .append(edetabel.leiaOsalejaKohtJuh(this.getNimi()) + 1)
                        .append(". ] ");
            }
        }

        String eriNimi = "";
        if (juhendaja) {
            eriNimi = " (Juh.)";
        }

        return String.format(
                """
                        %s%s, ID : %s
                        - Osaleb edetabelites :
                        %s
                        - Tulemused :
                        %s
                        - Kohad :
                          %s
                                                
                        - Kohad %s :
                          %s
                                                
                        - ELO :
                          %s. %.2fp
                        """,
                nimi, eriNimi, id, edetabeliteNimed, edetabeliteTulemused, edetabeliteKohad, eri, edetabeliteKohadEri, ELOedetabel.leiaOsalejaKoht(nimi) + 1, ELO);
    }
}
