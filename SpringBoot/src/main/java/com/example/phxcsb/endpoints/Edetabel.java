package com.example.phxcsb.endpoints;

import com.example.phxcsb.exceptions.ExceptionEdetabelPuudub;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

@RestController
public class Edetabel {
    private final JdbcTemplate jdbcTemplate;

    // Konstruktor
    public Edetabel(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Tagastab edetabeli sortimise viisi.
     * @param nimi - edetabeli nimi
     * @return - ORDER BY string.
     */
    private Object leiaOrdering(String nimi) {
        String query = "SELECT sortimine FROM edetabelid WHERE edetabel_nimi = ?";
        List<Map<String, Object>> data = jdbcTemplate.queryForList(query, nimi);
        return data.get(0).get("sortimine");
    }

    /**
     * Leiab antud edetabelist kõik skoorid (tabel: 'dataNew').
     * @param nimi - edetabel nimi.
     * @return - skoorid grupitud 'timestamp'-i järgi.
     */
    private List<Map<String, Object>> sqlLeiaEdetabeliSkooridNew(String nimi) {
        String query = String.format("""
                SELECT
                    d.osaleja, d.skoor, d.skoor2, d.aeg
                FROM
                    dataNew d
                WHERE
                    d.edetabel_nimi = ?
                ORDER BY
                    d.aeg asc
                    %s
                """, leiaOrdering(nimi));
        return jdbcTemplate.queryForList(query, nimi);
    }

    /**
     * Leiab antud edetabelist uusimad skoorid (tabel: 'dataNew').
     * @param nimi - edetabel nimi.
     * @return - skoorid grupitud 'timestamp'-i järgi.
     */
    private List<Map<String, Object>> sqlLeiaEdetabeliViimasedSkooridNew(String nimi) {
        // Query
        String query = String.format("""
                SELECT
                    d.osaleja, d.skoor, d.skoor2, d.aeg
                FROM
                    dataNew d
                INNER JOIN (
                    SELECT
                        edetabel_nimi, MAX(aeg) as max_aeg
                    FROM
                        dataNew
                    WHERE
                        edetabel_nimi = ?
                    GROUP BY
                        edetabel_nimi
                ) subquery ON d.edetabel_nimi = subquery.edetabel_nimi AND d.aeg = subquery.max_aeg
                WHERE
                    d.edetabel_nimi = ?
                ORDER BY
                    d.aeg DESC
                    %s
                """, leiaOrdering(nimi));
        return jdbcTemplate.queryForList(query, nimi, nimi);
    }

    /**
     * Vormistab saadud sql vastuse õigesse formaati.
     * @param data - sql data.
     * @return - vormistatud data.
     */
    private List<Map<String, Object>> formatData(List<Map<String, Object>> data) {
        long prgUnixTime = 0;

        List<Map<String, Object>> vormistatud = new ArrayList<>();

        Map<String, Object> root = new TreeMap<>();

        for (Map<String, Object> map : data) {
            Timestamp aeg = (Timestamp) map.get("aeg");
            long unixTime = aeg.getTime() / 1000;

            if (prgUnixTime != unixTime) {
                if (!root.isEmpty()) vormistatud.add(root);
                root = new TreeMap<>();

                root.put("timestamp", unixTime);
                root.put("results", new ArrayList<Map<String, Object>>());
                prgUnixTime = unixTime;
            }

            Map<String, Object> skoor = new TreeMap<>();
            skoor.put("skoor", map.get("skoor"));
            skoor.put("skoor2", map.get("skoor2"));
            skoor.put("pseudo", map.get("osaleja"));

            List<Map<String, Object>> results = (List<Map<String, Object>>) root.get("results");
            results.add(skoor);
        }
        vormistatud.add(root);

        return vormistatud;
    }

    @CrossOrigin(origins = {"http://localhost:3000", "https://phxc2.ee", "http://46.101.217.148:3000", "https://www.phxc2.ee"})
    @GetMapping(value = "/edetabel/{nimi}", produces = "application/json")
    private Object leiaEdetabel(@PathVariable String nimi, @RequestParam(required = false) String type) throws ExceptionEdetabelPuudub {
        if (nimi != null) {
            // Kui type pole antud ss võtame last.
            if (type == null) type = "last";

            // Kõik andmed
            if (type.equals("all")) {
                List<Map<String, Object>> data = sqlLeiaEdetabeliSkooridNew(nimi);

                // err : edetabelit pole
                if (data.isEmpty()) throw new ExceptionEdetabelPuudub("Edetabelit nimega \"" + nimi + "\" ei eksisteeri.");

                return ResponseEntity.status(HttpStatus.OK).body(formatData(data));
            }

            // Ainult kõige viimased sisestatud andmed
            if (type.equals("last")) {
                List<Map<String, Object>> data = sqlLeiaEdetabeliViimasedSkooridNew(nimi);

                // err : edetabelit pole
                if (data.isEmpty()) throw new ExceptionEdetabelPuudub("Edetabelit nimega \"" + nimi + "\" ei eksisteeri.");

                return ResponseEntity.status(HttpStatus.OK).body(formatData(data));
            }
            throw new ExceptionEdetabelPuudub("edetabelile ?type on andmata. (all|last)");
        }
        throw new ExceptionEdetabelPuudub("Edetabeli requesti vormistus on vale.");
    }
}
