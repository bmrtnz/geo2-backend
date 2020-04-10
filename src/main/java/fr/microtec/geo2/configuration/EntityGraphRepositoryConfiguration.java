package fr.microtec.geo2.configuration;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration of entity graph.
 */
@Configuration
@EnableJpaRepositories(
	repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class,
	basePackages = "fr.microtec.geo2"
)
public class EntityGraphRepositoryConfiguration {
}
