public class Osaleja {
    private String nimi;
    private float ELO;
    private Edetabel[] edetabelid = new Edetabel[0];  // todo: pole prg kasutusel.

    public Osaleja(String nimi) {
        this.nimi = nimi;
    }

    public void lisaEdetabelise(Edetabel edetabel) {
        // todo.
        System.out.println("todo.");

        Edetabel[] uus = new Edetabel[edetabelid.length + 1];
        System.arraycopy(edetabelid, 0, uus, 0, edetabelid.length);
        uus[uus.length - 1] = edetabel;
        this.edetabelid = uus;
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
}
