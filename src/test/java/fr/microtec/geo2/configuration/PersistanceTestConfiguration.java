package fr.microtec.geo2.configuration;

import fr.microtec.geo2.persistance.repository.GeoCustomRepositoryImpl;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@EnableJpaRepositories(basePackageClasses = GeoRepository.class, repositoryBaseClass = GeoCustomRepositoryImpl.class)
public class PersistanceTestConfiguration {}
