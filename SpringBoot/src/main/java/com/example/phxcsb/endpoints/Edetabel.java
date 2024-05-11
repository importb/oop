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

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/edetabel/{nimi}", produces = "application/json")
    private Object leiaEdetabel(@PathVariable String nimi, @RequestParam(required = false) String type) throws ExceptionEdetabelPuudub {
        if (nimi != null) {
            // Kui type pole antud ss võtame last.
            if (type == null) type = "last";

            // Kõik andmed
            if (type.equals("all")) {
                List<Map<String, Object>> data = sqlLeiaEdetabeliSkoorid(nimi);

                for(Map<String, Object> row : data) {
                    Timestamp aeg = (Timestamp) row.get("aeg");
                    long unixTime = aeg.getTime() / 1000;
                    row.put("aeg", unixTime);
                }

                if (data.isEmpty()) throw new ExceptionEdetabelPuudub("Edetabelit nimega \"" + nimi + "\" ei eksisteeri.");

                return ResponseEntity.status(HttpStatus.OK).body(data);
            }

            // Ainult kõige viimased sisestatud andmed
            if (type.equals("last")) {
                List<Map<String, Object>> data = sqlLeiaEdetabeliViimasedSkoorid(nimi);

                for(Map<String, Object> row : data) {
                    Timestamp aeg = (Timestamp) row.get("aeg");
                    long unixTime = aeg.getTime() / 1000;
                    row.put("aeg", unixTime);
                }

                if (data.isEmpty()) throw new ExceptionEdetabelPuudub("Edetabelit nimega \"" + nimi + "\" ei eksisteeri.");

                return ResponseEntity.status(HttpStatus.OK).body(data);
            }
            throw new ExceptionEdetabelPuudub("edetabelile ?type on andmata. (all|last)");
        }
        throw new ExceptionEdetabelPuudub("Edetabeli requesti vormistus on vale.");
    }
}
