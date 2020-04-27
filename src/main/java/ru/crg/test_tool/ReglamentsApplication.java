package ru.crg.test_tool;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.util.List;

@Log
@SpringBootApplication
public class ReglamentsApplication {

    private static final int BATCH_SIZE = 100;

    private final CrgHttpClient httpClient;
    private final DaoService daoService;
    private final Environment environment;
    private final CrgProperties crgProperties;

    public ReglamentsApplication(Environment environment,
                                 CrgHttpClient httpClient,
                                 CrgProperties crgProperties,
                                 DaoService daoService) {
        this.environment = environment;
        this.httpClient = httpClient;
        this.crgProperties = crgProperties;
        this.daoService = daoService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReglamentsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void appReady() {
        long totalStart = System.currentTimeMillis();

        log.info("Run for: " + environment.getProperty("spring.datasource.url"));
        log.info("With properties: " + crgProperties.toString());

        String schemaPrefix = crgProperties.getSchemaPrefix();
        daoService.recreateResultTable();
        daoService
                .getTargetTables(schemaPrefix, crgProperties.getReglamentColumnName())
                .stream().parallel()
                .forEach(this::handleTable);

        long failedLinks = daoService.countFailedLinks();

        long totalEnd = System.currentTimeMillis();
        log.info("Successfully with " + failedLinks + " failed links. Total spend time: " + (totalEnd - totalStart));
    }

    private void handleTable(TargetResource targetResource) {
        log.info("Handle schema: " + targetResource.toString());

        String reglamentColumnName = crgProperties.getReglamentColumnName();

        int offset = 0;
        while (true) {
            log.info(targetResource.toString() + " Handle batch: " + offset);
            long batchStart = System.currentTimeMillis();

            List<ReglamentLink> batchOfLinks = daoService.getLinks(targetResource, reglamentColumnName, BATCH_SIZE, offset);
            if (batchOfLinks.isEmpty()) {
                break;
            }

            // Обрабатываем
            checkAndResponse(batchOfLinks, targetResource);

            offset++;
            long batchFinish = System.currentTimeMillis();
            log.info("Average time for batch: " + (batchFinish - batchStart));
        }

        log.info("End handle schema: " + targetResource.toString());
    }

    private void checkAndResponse(List<ReglamentLink> links, TargetResource targetResource) {
        links.stream()
                .parallel()
                .filter(rLink -> {
                    if (rLink.getUrl() == null) {
                        daoService.saveIncorrectUrl(targetResource, rLink.getUrl(), "NULLABLE");

                        return false;
                    }

                    return true;
                })
                .forEach(rLink -> {
                    if (httpClient.isResourceAvailable(rLink.getUrl())) {
                        if (!crgProperties.isReportOnlyFailed()) {
                            log.info("Link OK: " + rLink.getUrl());
                            daoService.saveIncorrectUrl(targetResource, rLink.getUrl(), "OK");
                        }
                    } else {
                        log.info("Link UNAVAILABLE: " + rLink.getUrl());
                        daoService.saveIncorrectUrl(targetResource, rLink.getUrl(), "UNAVAILABLE");
                    }
                });
    }

}
