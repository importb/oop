package com.example.phxcsb.endpoints;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class Edetabel {
    private final JdbcTemplate jdbcTemplate;

    public Edetabel(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private String formatToJSON(List<Map<String, Object>> data) {
        StringBuilder vastus = new StringBuilder();
        vastus.append("[");

        long prgUnixTime = 0;


        for (Map<String, Object> row : data) {
            // aeg
            Timestamp aeg = (Timestamp) row.get("aeg");
            long unixTime = aeg.getTime() / 1000;

            if (prgUnixTime != unixTime) {
                if (vastus.charAt(vastus.length() - 1) == ',') vastus.deleteCharAt(vastus.length() - 1);
                if (vastus.charAt(vastus.length() - 1) != '[') vastus.append("]},");
                vastus.append("{");
                vastus.append("\"timestamp\":").append(unixTime).append(",");
                vastus.append("\"results\":[");
                prgUnixTime = unixTime;
            }


            // andmed
            Object osaleja = row.get("osaleja");
            Object skoor = row.get("skoor");
            Object skoor2 = row.get("skoor2");

            if (osaleja != null) osaleja = "\"" + osaleja + "\"";
            if (skoor != null) skoor = "\"" + skoor + "\"";

            vastus.append("{");

            vastus.append("\"pseudo\":").append(osaleja).append(",");
            vastus.append("\"skoor\":").append(skoor).append(",");
            vastus.append("\"skoor2\":").append(skoor2);

            vastus.append("},");
        }
        // eemaldame trailing koma.
        if (vastus.charAt(vastus.length() - 1) == ',') vastus.deleteCharAt(vastus.length() - 1);

        vastus.append("]}]");
        return vastus.toString();
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


    @GetMapping(value = "/edetabel/{nimi}", produces = "application/json")
    private Object leiaEdetabel(@PathVariable String nimi, @RequestParam(required = false) String type) {
        if (nimi != null) {
            if (type == null) type = "last";

            if (type.equals("all")) {
                List<Map<String, Object>> data = sqlLeiaEdetabeliSkoorid(nimi);
                return formatToJSON(data);
            }

            if (type.equals("last")) {
                List<Map<String, Object>> data = sqlLeiaEdetabeliViimasedSkoorid(nimi);
                return formatToJSON(data);
            }

            return "ERR: ei tunne type. Peab olema last|all.";
        }

        return "ERR: leidmiseks, pead andma edetabeli nime.";
    }
}
