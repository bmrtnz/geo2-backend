package fr.microtec.geo2.service.graphql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.GeoEntityGraph;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import fr.microtec.geo2.persistance.rsql.GeoCustomVisitor;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

/**
 * Abstract Geo graphQl service.
 *
 * @param <T> Entity type.
 * @param <ID> Entity id type.
 */
public abstract class GeoAbstractGraphQLService<T, ID extends Serializable> {

	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;

	protected final GeoGraphRepository<T, ID> repository;
	private RSQLParser rsqlParser;

	public GeoAbstractGraphQLService(GeoGraphRepository<T, ID> repository) {
		this.repository = repository;
	}

	protected RelayPage<T> getPage(String search, Pageable pageable, ResolutionEnvironment env) {
		Page<T> page;

		if (pageable == null) {
			pageable = PageRequest.of(0, 20);
		}

		if (search != null && !search.isBlank()) {
			page = this.repository.findAll(this.parseSearch(search), pageable, GeoEntityGraph.getEntityGraph(env));
		} else {
			page = this.repository.findAll(pageable, GeoEntityGraph.getEntityGraph(env));
		}

		return PageFactory.fromPage(page);
	}

	/**
	 * Get one entity by this id.
	 *
	 * @param id Entity id value.
	 * @param env GraphQL environment.
	 * @return Entity optional.
	 */
	protected Optional<T> getOne(ID id, ResolutionEnvironment env) {
		return this.repository.findById(id, GeoEntityGraph.getEntityGraph(env));
	}

	/**
	 * Merge entity from to entity to and return it.
	 * Propage null from graphQL environment.
	 *
	 * @param from From entity data.
	 * @param to Destination entity.
	 * @param env GraphQL environment.
	 * @return Merged entity data.
	 */
	public static <T> T merge(T from, T to, ResolutionEnvironment env) {
		// TODO filter null value from environment
		BeanWrapper src = new BeanWrapperImpl(from);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();
		String[] nullProps = Arrays.stream(pds)
				.filter(p -> src.getPropertyValue(p.getName()) == null)
				.map(FeatureDescriptor::getName)
				.toArray(String[]::new);

		BeanUtils.copyProperties(from, to, nullProps);

		return to;
	}

	/**
	 * Save entity.
	 *
	 * @param data Entity data to save.
	 * @return The saved entity.
	 */
	protected T save(T data) {
		ID id = (ID) this.getId(data);

		if (id != null) {
			Optional<T> optionalEntity = this.repository.findById(id);

			if (optionalEntity.isPresent()) {
				data = this.merge(data, optionalEntity.get(), null);
			}
		}

		return this.repository.save(data);
	}

	/**
	 * Extract id value from entity.
	 *
	 * @param entity Entity to extract id.
	 * @return Extracted id.
	 */
	protected Serializable getId(T entity) {
		MetamodelImplementor metamodel = (MetamodelImplementor) this.entityManagerFactory.getMetamodel();
		EntityPersister entityPersister = metamodel.entityPersister(entity.getClass());

		if (entityPersister.hasIdentifierProperty()) {
			return entityPersister.getIdentifier(entity, null);
		}

		return null;
	}

	/**
	 * Delete entity by id.
	 *
	 * @param id Entity id value.
	 * @return If delete has successfully.
	 */
	protected boolean delete(ID id) {
		try {
			this.repository.deleteById(id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Delete entity
	 *
	 * @param entity must not be {@literal null}
	 * @return success of the operation
	 */
	protected boolean delete(T entity) {
		try {
			this.repository.delete(entity);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Parse search string with RSQL and get specification.
	 *
	 * @param search Search string.
	 * @return Specification
	 */
	protected Specification<T> parseSearch(String search) {
		Node rootNode = this.rsqlParser.parse(search);

		return rootNode.accept(new GeoCustomVisitor<>());
	}

	@Autowired
	public final void setRSQLParser(RSQLParser rsqlParser) {
		this.rsqlParser = rsqlParser;
	}

}
