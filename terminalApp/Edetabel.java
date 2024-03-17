package terminalApp;

public class Edetabel {
    private String nimi;
    private int id;
    private Osaleja[] osalejad = new Osaleja[0];
    private float[] skoorid = new float[0];

    public Edetabel(int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }

    public int getId() {
        return id;
    }

    public String getNimi() {
        return nimi;
    }

    public Osaleja[] getOsalejad() {
        return osalejad;
    }

    public float[] getSkoorid() {
        return skoorid;
    }

    public String toString() {
        StringBuilder vastus = new StringBuilder("[ " + nimi + " ]\n");

        // osalejad ja nende skoorid
        for (int i = 0; i < osalejad.length; i++) {
            String spaces = " ".repeat(25 - osalejad[i].getNimi().length());
            vastus.append(String.format("%02d. %s%s%s ms\n", i + 1, osalejad[i].getNimi(), spaces, skoorid[i]));
        }

        return vastus.toString();
    }


    /**
     * Lisab osaleja selle edetabeli osalejate massiivi, kui teda veel ei ole lisatud.
     * Ehk sama nimega osalejaid ei saa olla.
     * @param osaleja - antud terminalApp.Osaleja
     */
    public void lisaOsaleja(Osaleja osaleja) {
        boolean jubaOlemas = false;

        for(Osaleja o : osalejad) {
            if (o.getNimi().equals(osaleja.getNimi())){
                jubaOlemas = true;
                break;
            }
        }

        if (!jubaOlemas) {
            Osaleja[] uus = new Osaleja[osalejad.length + 1];
            System.arraycopy(osalejad, 0, uus, 0, osalejad.length);
            uus[uus.length - 1] = osaleja;
            this.osalejad = uus;
        }
    }


    /**
     * Lisab skoori selle edetabeli skooridesse.
     * Otseselt ei seosta seda terminalApp.Osaleja objektiga, selle jaoks kasutada leiaOsalejaSkoor()
     * @param skoor - antud skoor.
     */
    public void lisaSkoor(float skoor) {
        float[] uus = new float[skoorid.length + 1];
        System.arraycopy(skoorid, 0, uus, 0, skoorid.length);
        uus[uus.length - 1] = skoor;
        this.skoorid = uus;
    }


    /**
     * Leiab osaleja skoori tema Osaleja objekti järgi.
     * @param osaleja - antud Osaleja
     * @return skoor selles edetabelis, juhul kui puudub siis tagastab null.
     */
    public Float leiaOsalejaSkoor(Osaleja osaleja) {
        int i = 0;
        boolean leitud = false;
        for(Osaleja o : osalejad){
            if (o.getNimi().equals(osaleja.getNimi())){
                leitud = true;
                break;
            }
            i++;
        }

        if (leitud) {
            return skoorid[i];
        }else{
            return null;
        }
    }

    /**
     * Leiab osaleja koha edetabelis.
     * @param osaleja - antud Osaleja
     * @param type - mis tüüpi edetabelit vaatame, 0 - kõik koos, 1 - ilma juhendajateta, 2 - ainult juhendajad.
     * @return - osaleja koht.
     */
    public Integer leiaOsalejaKoht(Osaleja osaleja, int type) {
        int i = 0;
        for(Osaleja o : osalejad){
            // Ilma juh.
            if (type == 1) if (o.isJuhendaja()) i--;
            // Ainult juh
            if (type == 2) if (!o.isJuhendaja()) i--;

            if (o.getNimi().equals(osaleja.getNimi())){
                return i;
            }
            i++;
        }
        return null;
    }

    /**
     * Leiab terminalApp.Osaleja objekti tema nime kaudu.
     * @param osalejaNimi - antud nimi
     * @return - terminalApp.Osaleja objekt
     */
    public Osaleja leiaOsaleja(String osalejaNimi) {
        for (Osaleja osaleja : osalejad) {
            if (osaleja.getRealNimi().equals(osalejaNimi)) {
                return osaleja;
            }
        }
        return null;
    }

}
