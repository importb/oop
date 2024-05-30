package com.example.phxcsb.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;


@RestController
class Ulesanded {
    private final JdbcTemplate jdbcTemplate;

    // Konstruktor
    public Ulesanded(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Tagastab JSON kõikide edetabelite nimede ja osalejate arvuga.
     * @return - JSON
     */
    @CrossOrigin(origins = {"http://localhost:3000", "https://phxc2.ee", "http://46.101.217.148:3000", "https://www.phxc2.ee"})
    @GetMapping(value = "/ulesanded", produces = "application/json")
    private Object leiaUlesanded() {
        String query = """
                SELECT 
                    df.edetabel_nimi,
                    COUNT(*) as userCount,
                    CASE 
                        WHEN NOW() > ed.deadline THEN true
                        ELSE false
                    END as finished
                FROM
                    dataNew df
                JOIN (
                    SELECT 
                        edetabel_nimi,
                        MAX(aeg) as newest_aeg
                    FROM 
                        dataNew
                    GROUP BY 
                        edetabel_nimi
                ) viimane ON df.edetabel_nimi = viimane.edetabel_nimi AND df.aeg = viimane.newest_aeg
                JOIN 
                    edetabelid ed ON df.edetabel_nimi = ed.edetabel_nimi
                GROUP BY 
                    df.edetabel_nimi, ed.deadline
                ORDER BY 
                    df.aeg;
                """;

        // Saadud vastus
        List<Map<String, Object>> sqlData = jdbcTemplate.queryForList(query);

        return ResponseEntity.status(HttpStatus.OK).body(sqlData);
    }
}
