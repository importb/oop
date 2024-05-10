package com.example.phxcsb.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;


@RestController
class Ulesanded {
    private final JdbcTemplate jdbcTemplate;

    public Ulesanded(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping(value = "/ulesanded", produces = "application/json")
    private Object leiaUlesanded() {
        // SQL query
        String query = """
                SELECT
                    edetabel_nimi,
                    count(koht) as userCount
                FROM
                	dataFinal
                GROUP BY
                	edetabel_nimi
                """;

        // Saadud vastus
        List<Map<String, Object>> sqlData = jdbcTemplate.queryForList(query);

        // Vormistame saadud vastuse JSON-iks.
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for(Map<String, Object> row : sqlData) {
            sb.append("{");

            sb.append("\"edetabelNimi\":\"").append(row.get("edetabel_nimi")).append("\",");
            sb.append("\"userCount\":").append(row.get("userCount"));

            sb.append("},");
        }

        // Eemaldame trailing koma ja lõpetame JSON-i ära.
        if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        return sb;
    }
}