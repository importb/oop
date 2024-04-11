package terminalApp;
import java.util.List;


public class Edetabel {
    private String nimi;
    private final int id;
    private List<String> osalejad;
    private List<List<String>> tulemused;
    private List<List<String>> skooriÜhikud;

    public Edetabel(int id) {
        this.id = id;
    }

    public void setOsalejad(List<String> osalejad) {
        this.osalejad = osalejad;
    }

    public void setTulemused(List<List<String>> tulemused) {
        this.tulemused = tulemused;
    }

    public void setSkooriÜhikud(List<List<String>> skooriÜhikud) {
        this.skooriÜhikud = skooriÜhikud;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public List<List<String>> getSkooriÜhikud() {
        return skooriÜhikud;
    }

    public List<String> getOsalejad() {
        return osalejad;
    }

    public List<String> leiaTulemus(int koht) {
        return tulemused.get(koht);
    }

    public List<String> leiaSkooriÜhik(int koht) {
        return skooriÜhikud.get(koht);
    }

    public List<String> leiaTulemus(Osaleja osaleja) {
        int i = 0;
        for(String osalejaNimi : osalejad) {
            if (osaleja.getNimi().equals(osalejaNimi.split(" ")[0])) {
                return tulemused.get(i);
            }
            i++;
        }

        return null;
    }

    public List<String> leiaSkooriÜhik(Osaleja osaleja) {
        return skooriÜhikud.get(leiaKoht(osaleja));
    }

    public int leiaKoht(Osaleja osaleja) {
        int i = 0;
        for(String osalejaNimi : osalejad) {
            if (osaleja.getNimi().equals(osalejaNimi.split(" ")[0])) {
                return i;
            }
            i++;
        }

        return -1;
    }

    public String toString() {
        StringBuilder read = new StringBuilder();

        for (int i = 0; i < osalejad.size(); i++) {
            String koht = i + 1 + ". ";
            read.append(koht);
            read.append(osalejad.get(i));

            StringBuilder tulemusedSõne = new StringBuilder();
            for (int j = 0; j < tulemused.get(i).size(); j++) {
                tulemusedSõne.append(tulemused.get(i).get(j)).append(" ");
                tulemusedSõne.append(skooriÜhikud.get(i).get(j)).append(" ".repeat(5));
            }

            read.append(" ".repeat(40 - osalejad.get(i).length() - koht.length()));
            read.append(tulemusedSõne);
            read.append("\n");
        }


        return String.format("""
                [ %s ]  (ID : %s)
                %s
                """, nimi, id, read);
    }
}
