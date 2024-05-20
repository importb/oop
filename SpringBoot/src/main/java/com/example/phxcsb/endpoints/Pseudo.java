package com.example.phxcsb.endpoints;

import com.example.phxcsb.exceptions.ExceptionOsalejaPuudub;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
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
    private double arvutaELO(List<Integer> vKohad, List<Integer> oKohad, List<Integer> edetabeliteID) {
        double elo = 1000;
        int index = 0;
        for (int id : edetabeliteID) {
            int viimaneKoht = vKohad.get(id - 1);
            int osalejaKoht = oKohad.get(index);

            elo += (100.0 - (double)viimaneKoht) / (double)osalejaKoht;

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
                    MAX(CASE WHEN edetabel_id = 8 THEN koht END) AS koht_8,
                    MAX(CASE WHEN edetabel_id = 9 THEN skoor2 END) AS skoor2_9,
                    MAX(CASE WHEN edetabel_id = 9 THEN edetabel_nimi END) AS edetabel_nimi_9,
                    MAX(CASE WHEN edetabel_id = 9 THEN koht END) AS koht_9
                FROM
                    dataFinal
                GROUP BY
                    osaleja;
                """;

        // vastus
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

        // Leiame iga osaleja ELO.
        List<Integer> vKohad = edetabeliteViimasedKohad();
        List<Map<String, Object>> vormistatud = new ArrayList<>();

        for (Map<String, Object> row : result) {
            List<Integer> oKohad = new ArrayList<>();
            List<Integer> edetabeliteID = new ArrayList<>();
            Map<String, Object> mapVormistatud = new HashMap<>();

            // osaleja nimi
            mapVormistatud.put("osaleja", row.get("osaleja"));

            for (int i = 1; i < 10; i++) {
                // kui osaleja ei osalenud edetabelis ss jätame vahele.
                if (row.get("edetabel_nimi_" + i) == null) continue;

                // edetabeli ID ja osaleja koht.
                edetabeliteID.add(i);
                oKohad.add(Math.toIntExact((long) row.get("koht_" + i)));
            }

            // arvutame ELO ja lisame mapi ja siis listi.
            mapVormistatud.put("ELO", arvutaELO(vKohad, oKohad, edetabeliteID));
            vormistatud.add(mapVormistatud);
        }

        return ResponseEntity.status(HttpStatus.OK).body(vormistatud);
    }

    @CrossOrigin(origins = {"http://localhost:3000", "http://phxc2.ee", "http://46.101.217.148:3000"})
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
                    MAX(CASE WHEN edetabel_id = 8 THEN koht END) AS koht_8,
                    MAX(CASE WHEN edetabel_id = 9 THEN skoor2 END) AS skoor2_9,
                    MAX(CASE WHEN edetabel_id = 9 THEN edetabel_nimi END) AS edetabel_nimi_9,
                    MAX(CASE WHEN edetabel_id = 9 THEN koht END) AS koht_9
                FROM
                    dataFinal
                GROUP BY
                    osaleja;
                """;

        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

        // vormistus
        List<Map<String, Object>> vormistatud = new ArrayList<>();

        // ELO arvutamine
        List<Integer> vKohad = edetabeliteViimasedKohad();

        for(Map<String, Object> row : result) {
            Map<String, Object> map = new HashMap<>();
            map.put("osaleja", row.get("osaleja"));

            List<Object> tulemused = new ArrayList<>();

            // elo
            List<Integer> oKohad = new ArrayList<>();
            List<Integer> edetabeliteID = new ArrayList<>();

            for (int i = 1; i < 10; i++) {
                if (row.get("edetabel_nimi_" + i) == null) continue;

                // elo
                edetabeliteID.add(i);
                oKohad.add(Math.toIntExact((long) row.get("koht_" + i)));

                Map<String, Object> edetabel = new HashMap<>();

                edetabel.put("edetabel_nimi", row.get("edetabel_nimi_" + i));
                edetabel.put("koht", row.get("koht_" + i));
                edetabel.put("skoor", row.get("skoor_" + i));
                edetabel.put("skoor2", row.get("skoor2_" + i));
                edetabel.put("skoor1ühik", ühikud[i - 1][0]);
                edetabel.put("skoor2ühik", ühikud[i - 1][1]);

                tulemused.add(edetabel);
            }

            map.put("results", tulemused);
            map.put("ELO", arvutaELO(vKohad, oKohad, edetabeliteID));

            vormistatud.add(map);
        }

        return ResponseEntity.status(HttpStatus.OK).body(vormistatud);
    }

    @GetMapping(value = "/osalejateEdetabel/{nimi}", produces = "application/json")
    private Object leiaOsaleja(@PathVariable String nimi) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<Map<String, Object>> data = sqlLeiaEdetabeliteSkoor(nimi);

        // Vormistame ümber
        List<Map<String, Object>> vormistatud = new ArrayList<>();
        Map<String, Object> root = new HashMap<>();

        // ERR.
        if (data.isEmpty()) throw new ExceptionOsalejaPuudub("Osalejat nimega: \"" + nimi + "\" ei eksisteeri andmebaasis.");

        // Osaleja nimi
        root.put("osaleja", data.get(0).get("osaleja"));

        // Osaleja ELO
        List<Integer> vKohad = edetabeliteViimasedKohad();
        List<Integer> oKohad = new ArrayList<>();
        List<Integer> edetabeliteID = new ArrayList<>();

        for (Map<String, Object> row : data) oKohad.add((Integer) row.get("koht"));
        for (Map<String, Object> row : data) edetabeliteID.add((Integer) row.get("edetabel_id"));

        root.put("ELO", arvutaELO(vKohad, oKohad, edetabeliteID));

        // Tulemused
        List<Object> tulemused = new ArrayList<>();

        for(Map<String, Object> row : data) {
            Map<String, Object> tulemus = new HashMap<>();

            tulemus.put("edetabel_nimi", row.get("edetabel_nimi"));
            tulemus.put("koht", row.get("koht"));
            tulemus.put("skoor", row.get("skoor"));
            tulemus.put("skoor2", row.get("skoor2"));

            int edetabel_id = (int) row.get("edetabel_id");
            tulemus.put("skoor1ühik", ühikud[edetabel_id - 1][0]);
            tulemus.put("skoor2ühik", ühikud[edetabel_id - 1][1]);

            tulemused.add(tulemus);
        }

        // Root vormistatud listi.
        root.put("results", tulemused);
        vormistatud.add(root);

        return ResponseEntity.status(HttpStatus.OK).body(vormistatud);
    }
}
