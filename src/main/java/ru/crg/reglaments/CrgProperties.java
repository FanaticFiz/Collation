package ru.crg.reglaments;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ConfigurationProperties(prefix = "crg-options")
public class CrgProperties {

    private String reglamentColumnName;
    private String schemaPrefix;

    private String protocol;
    private String host;
    private String port;

    private boolean reportOnlyFailed;

    public String getReglamentColumnName() {
        return Optional
                .ofNullable(reglamentColumnName)
                .orElseThrow(() -> new IllegalStateException("Provide reglamentColumnName"));
    }

    public void setReglamentColumnName(String reglamentColumnName) {
        this.reglamentColumnName = reglamentColumnName;
    }

    public String getSchemaPrefix() {
        return schemaPrefix;
    }

    public void setSchemaPrefix(String schemaPrefix) {
        this.schemaPrefix = schemaPrefix;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isReportOnlyFailed() {
        return reportOnlyFailed;
    }

    public void setReportOnlyFailed(boolean reportOnlyFailed) {
        this.reportOnlyFailed = reportOnlyFailed;
    }

    @Override
    public String toString() {
        return "CrgProperties{" +
                "reglamentColumnName='" + reglamentColumnName + '\'' +
                ", schemaPrefix='" + schemaPrefix + '\'' +
                ", protocol='" + protocol + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", reportOnlyFailed='" + reportOnlyFailed + '\'' +
                '}';
    }
}
