package fr.microtec.geo2.configuration;

import fr.microtec.geo2.persistance.repository.GeoCustomRepositoryFactoryBean;
import fr.microtec.geo2.persistance.repository.GeoCustomRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration of entity graph.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "fr.microtec.geo2",
    repositoryBaseClass = GeoCustomRepositoryImpl.class,
    repositoryFactoryBeanClass = GeoCustomRepositoryFactoryBean.class
)
@EnableJpaAuditing
public class PersistanceConfiguration {
}
