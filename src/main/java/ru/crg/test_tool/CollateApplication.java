package ru.crg.test_tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;

@Log4j2
@SpringBootApplication
public class CollateApplication {

    private final DaoService daoService;
    private final CrgProperties crgProperties;
    private final Environment environment;

    public CollateApplication(Environment environment,
                              CrgProperties crgProperties,
                              DaoService daoService) {
        this.daoService = daoService;
        this.environment = environment;
        this.crgProperties = crgProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(CollateApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void appReady() {
        log.info("Run for: " + environment.getProperty("spring.datasource.url"));

        daoService
                .getColumns(crgProperties.getSchemaPrefix())
                .stream().parallel()
                .forEach(this::handleColumn);
    }

    private void handleColumn(TargetResource resource) {
        log.info("Handle resource: " + resource.toString());

        try {
            daoService.updateCollation(resource, crgProperties.getCollation());
        } catch (DataAccessException e) {
            log.warn("Cant update collation: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Something went wrong: {}", e.getMessage());
        }
    }

}
