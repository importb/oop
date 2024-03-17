package terminalApp;

public class Osaleja {
    private int id;
    private String nimi;
    private int ELO;
    private Edetabel[] edetabelid = new Edetabel[0];
    private boolean juhendaja;

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
     * @return nimi
     */
    public String getNimi() {
        if (juhendaja){
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

        for(Edetabel edetabel : this.edetabelid){
            edetabelid.append("[ ").append(edetabel.getNimi()).append(" ] ");
        }

        return String.format(
                """
                %s, ID: %s
                - Osaleb edetabelites: %s
                - ELO: %s""",
            getNimi(), id, edetabelid, getELO()
        );
    }

    /**
     * Lisab edetabeli osaleja edetabli massiivi.
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
            int koht = edetabel.leiaOsalejaKoht(this);
            uusELO += eloTeenimine[Math.min(koht, eloTeenimine.length - 1)];
        }

        this.ELO = Math.max(0, uusELO);
    }
}
