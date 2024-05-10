package com.example.phxcsb.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
class Pseudo {
    private final JdbcTemplate jdbcTemplate;

    public Pseudo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Edetabelite ühikud [0] on skoor1 ja [1] on skoor2.
    private String[][] ühikud = {
            {"ms", null},
            {"ms", null},
            {"ms", null},
            {"ms", null},
            {null, null},
            {null, "ms"},
            {null, "ms"},
            {null, "ms"},
            {null, "ms"}
    };

    /**
     * ELO arvutamine.
     * @param vKohad - edetabelite kohtasid kokku ehk viimane koht.
     * @param oKohad - osaleja kohad edetabelites.
     * @param edetabeliteID - edetabelite ID-d kus osaleja osaleb.
     * @return - osaleja ELO.
     */
    private int arvutaELO(List<Integer> vKohad, List<Integer> oKohad, List<Integer> edetabeliteID) {
        int elo = 1000;
        int index = 0;
        for (int id : edetabeliteID) {
            int viimaneKoht = vKohad.get(id - 1);
            int osalejaKoht = oKohad.get(index);

            elo += (100 - viimaneKoht) / osalejaKoht;

            index++;
        }

        return elo;
    }

    /**
     * Leiab osaleja skoori kõikides edetabelites.
     * @param osaleja - antud osaleja nimi
     * @return - andmebaasist saadud vastus.
     */
    private List<Map<String, Object>> sqlLeiaEdetabeliteSkoor(String osaleja) {
        String query = """
                SELECT
                	koht,
                	osaleja,
                	skoor,
                	skoor2,
                	edetabel_nimi,
                    edetabel_id
                FROM
                	dataFinal
                WHERE
                	osaleja = ?
                """;

        return jdbcTemplate.queryForList(query, osaleja);
    }

    /**
     * Leiab kõikide edetabelite kohtade arvu.
     * @return - List kus on kõikide edetabelite kohtade arv ehk viimane koht.
     */
    private List<Integer> edetabeliteViimasedKohad() {
        List<Integer> kohad = new ArrayList<>();
        String query = """
                    SELECT
                        df.koht
                    FROM
                        dataFinal df
                    JOIN (
                        SELECT
                            edetabel_nimi,
                            MAX(koht) AS max_koht
                        FROM
                            dataFinal
                        GROUP BY
                            edetabel_nimi
                    ) max_kohts ON df.edetabel_nimi = max_kohts.edetabel_nimi AND df.koht = max_kohts.max_koht;
                """;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

        for (Map<String, Object> row : result) {
            kohad.add((Integer) row.get("koht"));
        }

        return kohad;
    }


    /**
     * ELO edetabel.
     * @return - JSON
     */
    @GetMapping(value = "/ELO", produces = "application/json")
    private Object leiaELOedetabel() {
        // päring
        String query = """
                SELECT
                    osaleja,
                    MAX(CASE WHEN edetabel_id = 1 THEN edetabel_nimi END) AS edetabel_nimi_1,
                    MAX(CASE WHEN edetabel_id = 1 THEN koht END) AS koht_1,
                    MAX(CASE WHEN edetabel_id = 2 THEN edetabel_nimi END) AS edetabel_nimi_2,
                    MAX(CASE WHEN edetabel_id = 2 THEN koht END) AS koht_2,
                    MAX(CASE WHEN edetabel_id = 3 THEN edetabel_nimi END) AS edetabel_nimi_3,
                    MAX(CASE WHEN edetabel_id = 3 THEN koht END) AS koht_3,
                    MAX(CASE WHEN edetabel_id = 4 THEN edetabel_nimi END) AS edetabel_nimi_4,
                    MAX(CASE WHEN edetabel_id = 4 THEN koht END) AS koht_4,
                    MAX(CASE WHEN edetabel_id = 5 THEN edetabel_nimi END) AS edetabel_nimi_5,
                    MAX(CASE WHEN edetabel_id = 5 THEN koht END) AS koht_5,
                    MAX(CASE WHEN edetabel_id = 6 THEN edetabel_nimi END) AS edetabel_nimi_6,
                    MAX(CASE WHEN edetabel_id = 6 THEN koht END) AS koht_6,
                    MAX(CASE WHEN edetabel_id = 7 THEN edetabel_nimi END) AS edetabel_nimi_7,
                    MAX(CASE WHEN edetabel_id = 7 THEN koht END) AS koht_7,
                    MAX(CASE WHEN edetabel_id = 8 THEN edetabel_nimi END) AS edetabel_nimi_8,
                    MAX(CASE WHEN edetabel_id = 8 THEN koht END) AS koht_8
                FROM
                    dataFinal
                GROUP BY
                    osaleja;
                """;

        // vastus
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

        // Edetabelite kohtade arv.
        List<Integer> vKohad = edetabeliteViimasedKohad();

        // Alustame JSON vormistusega.
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (Map<String, Object> row : result) {
            // ELO arvutamine
            List<Integer> oKohad = new ArrayList<>();
            List<Integer> edetabeliteID = new ArrayList<>();

            // Osaleja blokki algus
            sb.append("{");
            sb.append("\"osaleja\":\"").append(row.get("osaleja")).append("\",");

            // Edetabelite andmed
            for (int i = 1; i < 9; i++) {
                if (row.get("edetabel_nimi_" + i) == null) continue;

                // elo
                edetabeliteID.add(i);
                oKohad.add(Math.toIntExact((long) row.get("koht_" + i)));
            }

            // Lisame JSON-sse osaleja ELO.
            sb.append("\"ELO\":").append(arvutaELO(vKohad, oKohad, edetabeliteID));

            sb.append("},");
        }

        // Eemaldame trailing koma ning lõpetame JSON-i ära.
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb.toString();
    }


    @GetMapping(value = "/osalejateEdetabel", produces = "application/json")
    private Object leiaOsalejad() {
        // oh god, what have I created. kui on parem idee, ss fix :).
        String query = """
                SELECT
                    osaleja,
                    MAX(CASE WHEN edetabel_id = 1 THEN skoor END) AS skoor_1,
                    MAX(CASE WHEN edetabel_id = 1 THEN skoor2 END) AS skoor2_1,
                    MAX(CASE WHEN edetabel_id = 1 THEN edetabel_nimi END) AS edetabel_nimi_1,
                    MAX(CASE WHEN edetabel_id = 1 THEN koht END) AS koht_1,
                    MAX(CASE WHEN edetabel_id = 2 THEN skoor END) AS skoor_2,
                    MAX(CASE WHEN edetabel_id = 2 THEN skoor2 END) AS skoor2_2,
                    MAX(CASE WHEN edetabel_id = 2 THEN edetabel_nimi END) AS edetabel_nimi_2,
                    MAX(CASE WHEN edetabel_id = 2 THEN koht END) AS koht_2,
                    MAX(CASE WHEN edetabel_id = 3 THEN skoor END) AS skoor_3,
                    MAX(CASE WHEN edetabel_id = 3 THEN skoor2 END) AS skoor2_3,
                    MAX(CASE WHEN edetabel_id = 3 THEN edetabel_nimi END) AS edetabel_nimi_3,
                    MAX(CASE WHEN edetabel_id = 3 THEN koht END) AS koht_3,
                    MAX(CASE WHEN edetabel_id = 4 THEN skoor END) AS skoor_4,
                    MAX(CASE WHEN edetabel_id = 4 THEN skoor2 END) AS skoor2_4,
                    MAX(CASE WHEN edetabel_id = 4 THEN edetabel_nimi END) AS edetabel_nimi_4,
                    MAX(CASE WHEN edetabel_id = 4 THEN koht END) AS koht_4,
                    MAX(CASE WHEN edetabel_id = 5 THEN skoor END) AS skoor_5,
                    MAX(CASE WHEN edetabel_id = 5 THEN skoor2 END) AS skoor2_5,
                    MAX(CASE WHEN edetabel_id = 5 THEN edetabel_nimi END) AS edetabel_nimi_5,
                    MAX(CASE WHEN edetabel_id = 5 THEN koht END) AS koht_5,
                    MAX(CASE WHEN edetabel_id = 6 THEN skoor END) AS skoor_6,
                    MAX(CASE WHEN edetabel_id = 6 THEN skoor2 END) AS skoor2_6,
                    MAX(CASE WHEN edetabel_id = 6 THEN edetabel_nimi END) AS edetabel_nimi_6,
                    MAX(CASE WHEN edetabel_id = 6 THEN koht END) AS koht_6,
                    MAX(CASE WHEN edetabel_id = 7 THEN skoor END) AS skoor_7,
                    MAX(CASE WHEN edetabel_id = 7 THEN skoor2 END) AS skoor2_7,
                    MAX(CASE WHEN edetabel_id = 7 THEN edetabel_nimi END) AS edetabel_nimi_7,
                    MAX(CASE WHEN edetabel_id = 7 THEN koht END) AS koht_7,
                    MAX(CASE WHEN edetabel_id = 8 THEN skoor END) AS skoor_8,
                    MAX(CASE WHEN edetabel_id = 8 THEN skoor2 END) AS skoor2_8,
                    MAX(CASE WHEN edetabel_id = 8 THEN edetabel_nimi END) AS edetabel_nimi_8,
                    MAX(CASE WHEN edetabel_id = 8 THEN koht END) AS koht_8
                FROM
                    dataFinal
                GROUP BY
                    osaleja;
                """;

        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

        // format to json
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        // elo jaoks
        List<Integer> vKohad = edetabeliteViimasedKohad();

        for (Map<String, Object> row : result) {
            sb.append("{");
            sb.append("\"osaleja\":\"").append(row.get("osaleja")).append("\",");
            sb.append("\"results\":[");

            // elo
            List<Integer> oKohad = new ArrayList<>();
            List<Integer> edetabeliteID = new ArrayList<>();

            for (int i = 1; i < 9; i++) {
                if (row.get("edetabel_nimi_" + i) == null) continue;

                // elo
                edetabeliteID.add(i);
                oKohad.add(Math.toIntExact((long) row.get("koht_" + i)));

                // edetabeli info
                sb.append("{");
                sb.append("\"edetabel_nimi\":\"").append(row.get("edetabel_nimi_" + i)).append("\",");

                sb.append("\"koht\":").append(row.get("koht_" + i)).append(",");

                Object skoor = row.get("skoor_" + i);
                if (skoor != null) skoor = "\"" + skoor + "\"";
                sb.append("\"skoor\":").append(skoor).append(",");

                Object skoor2 = row.get("skoor2_" + i);
                if (skoor2 != null) skoor2 = "\"" + skoor2 + "\"";
                sb.append("\"skoor2\":").append(skoor2).append(",");

                Object skoor1ühik = ühikud[i - 1][0];
                if (skoor1ühik != null) skoor1ühik = "\"" + skoor1ühik + "\"";
                sb.append("\"skoor1ühik\":").append(skoor1ühik).append(",");

                Object skoor2ühik = ühikud[i - 1][1];
                if (skoor2ühik != null) skoor2ühik = "\"" + skoor2ühik + "\"";
                sb.append("\"skoor2ühik\":").append(skoor2ühik);

                sb.append("},");
            }

            // eemalda trailing koma
            sb.deleteCharAt(sb.length() - 1);

            // elo arvutamine
            int elo = arvutaELO(vKohad, oKohad, edetabeliteID);
            sb.append("],\"ELO\":").append(elo);

            sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb;
    }

    @GetMapping(value = "/osalejateEdetabel/{nimi}", produces = "application/json")
    private Object leiaOsaleja(@PathVariable String nimi) {
        StringBuilder sb = new StringBuilder();
        List<Map<String, Object>> edetabel = sqlLeiaEdetabeliteSkoor(nimi);

        // Lisa osaleja nimi.
        sb.append("[{\"osaleja\":\"").append(nimi).append("\",");

        // Lisa elo
        List<Integer> vKohad = edetabeliteViimasedKohad();
        List<Integer> oKohad = new ArrayList<>();
        List<Integer> edetabeliteID = new ArrayList<>();

        for (Map<String, Object> row : edetabel) oKohad.add((Integer) row.get("koht"));
        for (Map<String, Object> row : edetabel) edetabeliteID.add((Integer) row.get("edetabel_id"));

        int elo = arvutaELO(vKohad, oKohad, edetabeliteID);

        sb.append("\"ELO\":").append(elo).append(",");

        // Lisa edetabelite tulemused
        sb.append("\"results\":[");

        try {
            int i = 1;
            for (Map<String, Object> row : edetabel) {
                Object edetabel_nimi = row.get("edetabel_nimi");
                Object koht = row.get("koht");
                Object skoor = row.get("skoor");
                Object skoor2 = row.get("skoor2");
                Object skoor1ühik = ühikud[i - 1][0];
                Object skoor2ühik = ühikud[i - 1][1];

                if (edetabel_nimi != null) edetabel_nimi = "\"" + edetabel_nimi + "\"";
                if (skoor != null) skoor = "\"" + skoor + "\"";
                if (skoor2 != null) skoor2 = "\"" + skoor2 + "\"";
                if (skoor1ühik != null) skoor1ühik = "\"" + skoor1ühik + "\"";
                if (skoor2ühik != null) skoor2ühik = "\"" + skoor2ühik + "\"";

                sb.append("{");
                sb.append("\"edetabel_nimi\":").append(edetabel_nimi).append(",");
                sb.append("\"koht\":").append(koht).append(",");
                sb.append("\"skoor\":").append(skoor).append(",");
                sb.append("\"skoor2\":").append(skoor2).append(",");
                sb.append("\"skoor1ühik\":").append(skoor1ühik).append(",");
                sb.append("\"skoor2ühik\":").append(skoor2ühik);
                sb.append("},");
                i += 1;
            }
        } catch (Exception ignored) {
            // list oli empty.
        }

        // end
        if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        sb.append("}]");

        return sb.toString();
    }
}
