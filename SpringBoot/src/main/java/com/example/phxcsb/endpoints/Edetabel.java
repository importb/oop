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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Edetabel {
    private final JdbcTemplate jdbcTemplate;

    // Konstruktor
    public Edetabel(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private List<Map<String, Object>> sqlLeiaEdetabeliSkoorid(String nimi) {
        String query = """
                SELECT
                    osaleja, skoor, skoor2, aeg
                FROM
                    data
                WHERE
                    edetabel_nimi = ?
                ORDER BY
                    aeg asc
                """;
        return jdbcTemplate.queryForList(query, nimi);
    }

    private List<Map<String, Object>> sqlLeiaEdetabeliViimasedSkoorid(String nimi) {
        String query = """
                SELECT
                	osaleja, skoor, skoor2, aeg
                FROM
                	data d
                WHERE
                	aeg IN (
                		SELECT
                			MAX(aeg)
                		FROM
                			data
                		WHERE
                			edetabel_nimi = ?
                		) AND
                	edetabel_nimi = ?
                ORDER BY
                	aeg desc
                """;
        return jdbcTemplate.queryForList(query, nimi, nimi);
    }

    private List<Map<String, Object>> sqlLeiaEdetabeliSkooridNew(String nimi) {
        String query = """
                SELECT
                    osaleja, skoor, skoor2, aeg
                FROM
                    dataNew
                WHERE
                    edetabel_nimi = ?
                ORDER BY
                    aeg asc
                """;
        return jdbcTemplate.queryForList(query, nimi);
    }

    private List<Map<String, Object>> sqlLeiaEdetabeliViimasedSkooridNew(String nimi) {
        String query = """
                SELECT
                	osaleja, skoor, skoor2, aeg
                FROM
                	dataNew d
                WHERE
                	aeg IN (
                		SELECT
                			MAX(aeg)
                		FROM
                			dataNew
                		WHERE
                			edetabel_nimi = ?
                		) AND
                	edetabel_nimi = ?
                ORDER BY
                	aeg desc
                """;
        return jdbcTemplate.queryForList(query, nimi, nimi);
    }

    private List<Map<String, Object>> formatData(List<Map<String, Object>> data) {
        long prgUnixTime = 0;

        List<Map<String, Object>> vormistatud = new ArrayList<>();

        Map<String, Object> root = new HashMap<>();

        for (Map<String, Object> map : data) {
            Timestamp aeg = (Timestamp) map.get("aeg");
            long unixTime = aeg.getTime() / 1000;

            if (prgUnixTime != unixTime) {
                if (!root.isEmpty()) vormistatud.add(root);
                root = new HashMap<>();

                root.put("timestamp", unixTime);
                root.put("results", new ArrayList<Map<String, Object>>());
                prgUnixTime = unixTime;
            }

            Map<String, Object> skoor = new HashMap<>();
            skoor.put("skoor", map.get("skoor"));
            skoor.put("skoor2", map.get("skoor2"));
            skoor.put("pseudo", map.get("osaleja"));

            List<Map<String, Object>> results = (List<Map<String, Object>>) root.get("results");
            results.add(skoor);
        }
        vormistatud.add(root);

        return vormistatud;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/edetabel/{nimi}", produces = "application/json")
    private Object leiaEdetabel(@PathVariable String nimi, @RequestParam(required = false) String type) throws ExceptionEdetabelPuudub {
        if (nimi != null) {
            // Kui type pole antud ss võtame last.
            if (type == null) type = "last";

            // Kõik andmed
            if (type.equals("all")) {
                List<Map<String, Object>> data;
                if (nimi.equals("algarvuringid")) {
                    data = sqlLeiaEdetabeliSkooridNew(nimi);
                }else {
                    data = sqlLeiaEdetabeliSkoorid(nimi);
                }

                // err : edetabelit pole
                if (data.isEmpty()) throw new ExceptionEdetabelPuudub("Edetabelit nimega \"" + nimi + "\" ei eksisteeri.");

                return ResponseEntity.status(HttpStatus.OK).body(formatData(data));
            }

            // Ainult kõige viimased sisestatud andmed
            if (type.equals("last")) {
                List<Map<String, Object>> data;
                if (nimi.equals("algarvuringid")) {
                    data = sqlLeiaEdetabeliViimasedSkooridNew(nimi);
                }else {
                    data = sqlLeiaEdetabeliViimasedSkoorid(nimi);
                }

                // err : edetabelit pole
                if (data.isEmpty()) throw new ExceptionEdetabelPuudub("Edetabelit nimega \"" + nimi + "\" ei eksisteeri.");

                return ResponseEntity.status(HttpStatus.OK).body(formatData(data));
            }
            throw new ExceptionEdetabelPuudub("edetabelile ?type on andmata. (all|last)");
        }
        throw new ExceptionEdetabelPuudub("Edetabeli requesti vormistus on vale.");
    }
}
