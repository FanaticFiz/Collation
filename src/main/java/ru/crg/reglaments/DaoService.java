package ru.crg.reglaments;

import lombok.extern.java.Log;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static ru.crg.reglaments.Mapper.mapToReglamentLink;

@Log
@Service
public class DaoService {

    private final JdbcTemplate jdbcTemplate;

    public DaoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TargetResource> getTargetTables(String schemaPrefix, String columnName) {
        String sql = "SELECT table_schema, table_name FROM information_schema.columns " +
                "WHERE table_schema LIKE '" + schemaPrefix + "%' " +
                "AND table_name NOT LIKE '%_extension' AND column_name = '" + columnName + "'";

        List<TargetResource> targetTables = jdbcTemplate
                .queryForList(sql)
                .stream()
                .map(tMap -> new TargetResource(tMap.get("table_schema").toString(), tMap.get("table_name").toString()))
                .collect(Collectors.toList());

        log.info("There are " + targetTables.size() + " suitable tables");

        return targetTables;
    }

    public List<ReglamentLink> getLinks(TargetResource resource, String columnName, int limit, int offset) {
        String sql = String.format("SELECT %s FROM %s.%s LIMIT ? OFFSET ?",
                columnName, resource.getSchemaName(), resource.getTableName());

        return jdbcTemplate.queryForList(sql, limit, limit * offset).stream()
                .map(oMap -> mapToReglamentLink(oMap, columnName))
                .collect(Collectors.toList());
    }

    public void recreateResultTable() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS public.test_reglament_results");
        jdbcTemplate.execute("CREATE TABLE public.test_reglament_results (" +
                "id bigserial NOT NULL, " +
                "url character varying(1024), " +
                "resource character varying, " +
                "status character varying, " +
                "CONSTRAINT test_reglament_results_pkey PRIMARY KEY (id)) " +
                "TABLESPACE pg_default");
        jdbcTemplate.execute("ALTER TABLE public.test_reglament_results OWNER to fiz;");
    }

    public void saveIncorrectUrl(TargetResource targetResource, @Nullable URL url, String status) {
        String resourceIdentifier = targetResource.getSchemaName() + ":" + targetResource.getTableName();

        String sUrl = null;
        if (url != null) {
            sUrl = url.toString();
        }

        jdbcTemplate.update("INSERT INTO public.test_reglament_results(url, resource, status) VALUES (?, ?, ?)",
                sUrl, resourceIdentifier, status);
    }

    public long countFailedLinks() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM public.test_reglament_results WHERE status != 'OK'", Long.class);
    }

}
