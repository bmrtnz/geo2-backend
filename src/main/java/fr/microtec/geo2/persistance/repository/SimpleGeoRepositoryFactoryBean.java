package fr.microtec.geo2.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class SimpleGeoRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable>
	extends JpaRepositoryFactoryBean<R, T, I> {

	/**
	 * Creates a new {@link JpaRepositoryFactoryBean} for the given repository interface.
	 *
	 * @param repositoryInterface must not be {@literal null}.
	 */
	public SimpleGeoRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}

	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new SimpleGeoRepositoryFactory(this.getRepositoryInformation().getDomainType(), entityManager);
	}

	private static class SimpleGeoRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {
		private final Class<T> domainClass;
		private final EntityManager entityManager;

		public SimpleGeoRepositoryFactory(Class<T> domainClass, EntityManager entityManager) {
			super(entityManager);

			this.domainClass = domainClass;
			this.entityManager = entityManager;
		}

		protected Object getTargetRepository(RepositoryMetadata metadata) {
			return new SimpleGeoRepositoryFactory<T, I>((Class<T>) metadata.getDomainType(), entityManager);
		}

		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return SimpleGeoRepository.class;
		}
	}
}
