package terminalApp;

public class Osaleja {
    private final int id;
    private final String nimi;
    private final boolean juhendaja;
    private int ELO;
    private Edetabel[] edetabelid = new Edetabel[0];


    public Osaleja(int id, String nimi, boolean juhendaja) {
        this.id = id;
        this.nimi = nimi;
        this.juhendaja = juhendaja;
    }

    public boolean isJuhendaja() {
        return juhendaja;
    }

    public int getId() {
        return id;
    }

    public int getELO() {
        return ELO;
    }

    /**
     * Tagastab nime, kui juhendaja siis tagastab nimi + (Juh).
     * Kui otsid juhendajat nime pidi ss kindlasti kasuta getRealNimi()
     *
     * @return nimi
     */
    public String getNimi() {
        if (juhendaja) {
            return nimi + " (Juh)";
        }
        return nimi;
    }

    public String getRealNimi() {
        return nimi;
    }

    public Edetabel[] getEdetabelid() {
        return edetabelid;
    }

    public String toString() {
        StringBuilder edetabelid = new StringBuilder();
        int kohtadeSumma = 0;
        int tulemusiKokku = 0;
        int erikohtadeSumma = 0;
        StringBuilder tulemused = new StringBuilder();
        StringBuilder kohad = new StringBuilder();
        StringBuilder kohadEri = new StringBuilder();

        for (Edetabel edetabel : this.edetabelid) {
            // Edetabelite nimed
            if (!edetabel.getNimi().equals("ELO")) {
                edetabelid.append("\n  [ ").append(edetabel.getNimi()).append(" ] ");

                // Keskmise koha arvutamine (peamine edetabel)
                kohtadeSumma += edetabel.leiaOsalejaKoht(this, 0) + 1;
                tulemusiKokku++;

                // Edetabeli skoor osalejal
                if (edetabel.leiaOsalejaKoht(this, 0) == 0){
                    String tekst = String.format("\n  %s ms", edetabel.leiaOsalejaSkoor(this));
                    String vahed = " ".repeat(16 - tekst.length());
                    String tekstFinal = tekst + vahed + "(Parim tulemus edetabelis!)";

                    tulemused.append(tekstFinal);
                }else {
                    Osaleja esimene = edetabel.getOsalejad()[0];
                    float osalejaSkoor = edetabel.leiaOsalejaSkoor(this);
                    float parimTulemus = edetabel.leiaOsalejaSkoor(esimene);
                    float aeglasemProtsent = (osalejaSkoor / parimTulemus) * 100;

                    String tekst = String.format("\n  %s ms", osalejaSkoor);
                    String vahed = " ".repeat(16 - tekst.length());
                    String tekstFinal = tekst + vahed + "(Parim: " + parimTulemus + " ms, " + (int)aeglasemProtsent + "% kiirem)";

                    tulemused.append(tekstFinal);
                }

                // Edetabeli koht osalejal (peamine edetabel)
                kohad.append(String.format("[ %s. ] ", edetabel.leiaOsalejaKoht(this, 0) + 1));

                // Juhendaja vaba edetabeli kohad ja keskmine koht
                if (!juhendaja) {
                    erikohtadeSumma += edetabel.leiaOsalejaKoht(this, 1) + 1;
                    kohadEri.append(String.format("[ %s. ] ", edetabel.leiaOsalejaKoht(this, 1) + 1));
                }
                // Juhendajate edetabeli koht ja keskmine koht
                if (juhendaja) {
                    erikohtadeSumma += edetabel.leiaOsalejaKoht(this, 2) + 1;
                    kohadEri.append(String.format("[ %s. ] ", edetabel.leiaOsalejaKoht(this, 2) + 1));
                }
            }
        }

        String eriKohadeTekst = "ilma juh. :";
        if (juhendaja) eriKohadeTekst = "juh. :";

        int ELOkoht = this.edetabelid[this.edetabelid.length - 1].leiaOsalejaKoht(this, 0) + 1;


        return String.format(
                """
                        %s, ID: %s
                        - Osaleb edetabelites : %s
                        
                        - Tulemused : %s
                                
                        - Kohad : (Keskmine koht: %s.)
                          %s
                                        
                        - Kohad %s (Keskmine koht: %s.)
                          %s
                                        
                        - ELO :
                          %s. %.1fp""",
                getNimi(), id, edetabelid, tulemused, kohtadeSumma / tulemusiKokku, kohad, eriKohadeTekst, erikohtadeSumma / tulemusiKokku, kohadEri, ELOkoht, (float)getELO()
        );
    }

    /**
     * Lisab edetabeli osaleja edetabli massiivi.
     *
     * @param edetabel - antud Edetabel
     */
    public void lisaEdetabelise(Edetabel edetabel) {
        Edetabel[] uus = new Edetabel[edetabelid.length + 1];
        System.arraycopy(edetabelid, 0, uus, 0, edetabelid.length);
        uus[uus.length - 1] = edetabel;
        this.edetabelid = uus;
    }

    /**
     * Arvutab osaleja ELO põhinedes tema kohale edetabelites.
     * 1. koht annab 100p ELO-le juurde.
     * 30. koht ja alla poole kaotavad 50p oma ELO-st.
     */
    public void arvutaELO() {
        int uusELO = 1000;
        int[] eloTeenimine = {100, 90, 80, 70, 60, 50, 40, 30, 20, 10, 5, 4, 3, 2, 1, 0, -1, -2, -3, -4, -5, -10, -15, -20, -25, -30, -35, -40, -45, -50};

        for (Edetabel edetabel : this.getEdetabelid()) {
            int koht = edetabel.leiaOsalejaKoht(this, 0);
            uusELO += eloTeenimine[Math.min(koht, eloTeenimine.length - 1)];
        }

        this.ELO = Math.max(0, uusELO);
    }
}
