package ru.crg.test_tool;

import lombok.extern.java.Log;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log
@Service
public class DaoService {

    private final JdbcTemplate jdbcTemplate;

    public DaoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TargetResource> getTables(String schemaPrefix) {
        String sql = "SELECT table_schema, table_name, column_name, character_maximum_length " +
                "FROM information_schema.columns " +
                "WHERE table_schema LIKE '" + schemaPrefix + "%' AND data_type = 'character varying'";

        List<TargetResource> targetColumns = jdbcTemplate
                .queryForList(sql)
                .stream()
                .map(o -> {
                    final Object charLength = o.get("character_maximum_length");

                    return new TargetResource(
                            o.get("table_schema").toString(),
                            o.get("table_name").toString(),
                            o.get("column_name").toString(),
                            charLength == null ? null : charLength.toString());
                }).collect(Collectors.toList());

        log.info("There are " + targetColumns.size() + " suitable columns");

        return targetColumns;
    }

    public void updateCollation(TargetResource resource, String collation) {
        String charLength;
        if (resource.getCharLength() == null) {
            charLength = "";
        } else {
            charLength = "(" + resource.getCharLength() + ")";
        }

        String sql = String.format("ALTER TABLE %s.%s ALTER COLUMN \"%s\" " +
                        "SET DATA TYPE character varying%s COLLATE \"%s\"",
                resource.getSchemaName(), resource.getTableName(), resource.getColumnName(), charLength, collation);

        jdbcTemplate.execute(sql);
    }

}
