package fr.microtec.geo2.service;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import fr.microtec.geo2.persistance.GeoEntityGraph;
import fr.microtec.geo2.persistance.repository.GeoGraphRepository;
import fr.microtec.geo2.persistance.rsql.GeoCustomVisitor;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Abstract Geo graphQl service.
 *
 * @param <T> Entity type.
 * @param <ID> Entity id type.
 */
public abstract class GeoAbstractGraphQlService<T, ID extends Serializable> {

	private final GeoGraphRepository<T, ID> repository;
	private RSQLParser rsqlParser;

	public GeoAbstractGraphQlService(GeoGraphRepository<T, ID> repository) {
		this.repository = repository;
	}

	protected Page<T> getPage(String search, ResolutionEnvironment env) {
		return this.getPage(search, PageRequest.of(0, 10), env);
	}

	protected Page<T> getPage(String search, Pageable pageable, ResolutionEnvironment env) {
		if (search != null && !search.isBlank()) {
			return this.repository.findAll(this.parseSearch(search), pageable, GeoEntityGraph.getEntityGraph(env));
		}

		return this.repository.findAll(pageable, GeoEntityGraph.getEntityGraph(env));
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
	 * @param entity Entity to save.
	 * @return The saved entity.
	 */
	protected T save(T entity) {
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
