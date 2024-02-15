package fr.microtec.geo2.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import fr.microtec.geo2.persistance.repository.GeoCustomRepositoryImpl;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@TestConfiguration
@EnableJpaRepositories(basePackageClasses = GeoRepository.class, repositoryBaseClass = GeoCustomRepositoryImpl.class)
public class PersistanceTestConfiguration {
}
