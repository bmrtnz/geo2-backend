package fr.microtec.geo2.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "geo2")
public class Geo2Configuration {

    @Data
    @ConfigurationProperties(prefix = "geo2")
    private final class Geo2Properties {

        /** Enable API benchmark/logs */
        private Boolean enableApiBenchmark = false;

    }
}
