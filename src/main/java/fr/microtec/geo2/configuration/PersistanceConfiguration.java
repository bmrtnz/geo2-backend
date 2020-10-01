package fr.microtec.geo2.configuration;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import fr.microtec.geo2.persistance.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration of entity graph.
 */
@Configuration
@EnableJpaRepositories(
	repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class,
	basePackages = "fr.microtec.geo2"
)
@EnableJpaAuditing
public class PersistanceConfiguration {
}
