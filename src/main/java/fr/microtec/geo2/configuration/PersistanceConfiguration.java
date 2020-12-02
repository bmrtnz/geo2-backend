package fr.microtec.geo2.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration of entity graph.
 */
@Configuration
@EnableJpaRepositories(basePackages = "fr.microtec.geo2")
@EnableJpaAuditing
public class PersistanceConfiguration {
}
