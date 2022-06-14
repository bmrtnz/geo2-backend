package fr.microtec.geo2.configuration;

import fr.microtec.geo2.persistance.repository.GeoCustomRepositoryFactoryBean;
import fr.microtec.geo2.persistance.repository.GeoCustomRepositoryImpl;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import fr.microtec.geo2.persistance.repository.GeoRepositoryEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@Import(GeoRepositoryEvent.class)
@EnableJpaRepositories(basePackageClasses = GeoRepository.class, repositoryBaseClass = GeoCustomRepositoryImpl.class, repositoryFactoryBeanClass = GeoCustomRepositoryFactoryBean.class)
public class PersistanceTestConfiguration {}
