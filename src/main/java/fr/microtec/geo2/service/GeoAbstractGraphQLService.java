package fr.microtec.geo2.service;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.GeoEntityGraph;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import fr.microtec.geo2.persistance.rsql.GeoCustomVisitor;
import io.leangen.graphql.execution.ResolutionEnvironment;
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
	 * Save entity.
	 *
	 * @param data Entity data to save.
	 * @return The saved entity.
	 */
	protected T save(T data) {
		T entity = this.repository.getOne((ID) this.entityManagerFactory.getPersistenceUnitUtil().getIdentifier(data));

		BeanWrapper src = new BeanWrapperImpl(data);
		PropertyDescriptor[] pds = src.getPropertyDescriptors();
		String[] nullProps = Arrays.stream(pds)
				.filter(p -> src.getPropertyValue(p.getName()) == null)
				.map(FeatureDescriptor::getName)
				.toArray(String[]::new);

		BeanUtils.copyProperties(data, entity, nullProps);

		return this.repository.save(entity);
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
	 * Parse search string with RSQL and get specification.
	 *
	 * @param search Search string.
	 * @return Specification
	 */
	private Specification<T> parseSearch(String search) {
		Node rootNode = this.rsqlParser.parse(search);

		return rootNode.accept(new GeoCustomVisitor<>());
	}

	@Autowired
	public final void setRSQLParser(RSQLParser rsqlParser) {
		this.rsqlParser = rsqlParser;
	}

}
