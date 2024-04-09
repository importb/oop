package terminalApp;

import java.util.ArrayList;
import java.util.List;

public class Osaleja {
    private final int id;
    private final String nimi;
    private final boolean juhendaja;
    private List<Edetabel> edetabelid = new ArrayList<>();
    private Edetabel ELOEdetabel;

    public Osaleja(int id, String nimi, boolean juhendaja) {
        this.id = id;
        this.nimi = nimi;
        this.juhendaja = juhendaja;
    }

    public String getNimi() {
        return nimi;
    }

    public int getId() {
        return id;
    }

    public boolean isJuhendaja() {
        return juhendaja;
    }

    public static double easeInCubic(double x) {
        return x * x * x;
    }

    /**
     * Arvutab osaleja ELO põhinedes tema kohale edetabelites.
     * ELO saamine edetabeli kohta oleneb osaleja kohast edetabelis ning palju tulemusi edetabelis on.
     * Valisime EaseInCubic, kuna see tundub kõige parem tasakaal koha ning
     * mitmes edetabelis osalenud on vahel.
     */
    public float arvutaELO() {
        float ELO = 100;

        for (Edetabel edetabel : edetabelid) {
            int koht = edetabel.leiaKoht(this);
            int kohtiKokku = edetabel.getOsalejad().size();

            ELO += (float) easeInCubic(0.8 - ((double) koht / kohtiKokku)) * 45;
        }

        return ELO;
    }

    public void lisaEdetabel(Edetabel edetabel) {
        edetabelid.add(edetabel);
    }

    public void lisaELOEdetabel(Edetabel edetabel) {
        ELOEdetabel = edetabel;
    }

    public String toString() {
        // Edetabelid
        StringBuilder edetabeliteNimed = new StringBuilder();
        StringBuilder edetabeliteKohad = new StringBuilder();
        List<List<String>> edetabeliteSkoorid = new ArrayList<>();
        List<List<String>> edetabeliteSkooriÜhikud = new ArrayList<>();

        List<List<String>> parimadSkoorid = new ArrayList<>();

        for (Edetabel edetabel : edetabelid) {
            edetabeliteNimed.append("  [ ").append(edetabel.getNimi()).append(" ]\n");
            edetabeliteSkoorid.add(edetabel.leiaTulemus(this));
            edetabeliteSkooriÜhikud.add(edetabel.leiaSkooriÜhik(this));
            edetabeliteKohad.append("[ ").append(edetabel.leiaKoht(this) + 1).append(". ] ");

            parimadSkoorid.add(edetabel.leiaTulemus(0));
        }

        StringBuilder edetabeliteTulemused = new StringBuilder();

        for (int i = 0; i < edetabeliteSkoorid.size(); i++) {
            List<String> tulemus = new ArrayList<>();
            List<String> parimTulemus = new ArrayList<>();

            for (int j = 0; j < edetabeliteSkoorid.get(i).size(); j++) {
                String t = edetabeliteSkoorid.get(i).get(j) + " " + edetabeliteSkooriÜhikud.get(i).get(j);
                tulemus.add(t.trim());

                String w = parimadSkoorid.get(i).get(j) + " " + edetabeliteSkooriÜhikud.get(i).get(j);
                parimTulemus.add(w.trim());
            }

            // Tulemus
            edetabeliteTulemused.append("  ").append(tulemus);

            // Parim tulemus
            edetabeliteTulemused.append(" ".repeat(18 - String.valueOf(tulemus).length()));
            if (!parimTulemus.equals(tulemus)) {
                edetabeliteTulemused.append("(Parim : ").append(parimTulemus).append(")");
            } else {
                edetabeliteTulemused.append("(Parim või samaväärne tulemus!)");
            }

            edetabeliteTulemused.append("\n");
        }

        // Juhendaja
        String lisa = "";
        if (juhendaja) lisa = " (Juh.)";


        return String.format("""
                        %s%s, ID : %s
                        - Osaleb edetabelites :
                        %s
                        - Tulemused :
                        %s
                        - Kohad :
                          %s
                          
                        - ELO :
                          %s. %s %s
                        """,
                nimi,
                lisa,
                id,
                edetabeliteNimed,
                edetabeliteTulemused,
                edetabeliteKohad,
                ELOEdetabel.leiaKoht(this) + 1,
                ELOEdetabel.leiaTulemus(this).getFirst(),
                ELOEdetabel.getSkooriÜhikud().getFirst().getFirst());
    }
}
