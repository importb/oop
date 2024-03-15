
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

    public void lisaOsaleja(Osaleja osaleja) {
        Osaleja[] uus = new Osaleja[osalejad.length + 1];
        System.arraycopy(osalejad, 0, uus, 0, osalejad.length);
        uus[uus.length - 1] = osaleja;
        this.osalejad = uus;
    }

    public void lisaSkoor(float skoor) {
        float[] uus = new float[skoorid.length + 1];
        System.arraycopy(skoorid, 0, uus, 0, skoorid.length);
        uus[uus.length - 1] = skoor;
        this.skoorid = uus;
    }

    public Osaleja leiaOsaleja(String osalejaNimi) {
        for (Osaleja osaleja : osalejad) {
            if (osaleja.getNimi().equals(osalejaNimi)) {
                return osaleja;
            }
        }
        return null;
    }


    public String toString() {
        StringBuilder vastus = new StringBuilder("[ " + nimi + " ]\n");

        // osalejad ja nende skoorid
        for (int i = 0; i < osalejad.length; i++) {
            String spaces = " ".repeat(25 - osalejad[i].getNimi().length());

            vastus.append(String.format("%02d. %s%s%s ms\n", i + 1, osalejad[i], spaces, skoorid[i]));
        }

        return vastus.toString();
    }
}
