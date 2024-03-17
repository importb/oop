public class Osaleja {
    private int id;
    private String nimi;
    private int ELO;
    private Edetabel[] edetabelid = new Edetabel[0];

    public Osaleja(int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }

    public int getId() {
        return id;
    }

    public int getELO() {
        return ELO;
    }

    public String getNimi() {
        return nimi;
    }

    public Edetabel[] getEdetabelid() {
        return edetabelid;
    }

    public String toString() {
        return nimi;
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
