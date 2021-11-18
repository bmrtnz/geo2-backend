package fr.microtec.geo2.service.graphql;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.configuration.graphql.Summary;
import fr.microtec.geo2.configuration.graphql.SummaryType;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import fr.microtec.geo2.persistance.rsql.GeoCustomVisitor;
import io.leangen.graphql.execution.ResolutionEnvironment;
import lombok.val;

/**
 * Abstract Geo graphQl service.
 *
 * @param <T> Entity type.
 * @param <ID> Entity id type.
 */
public abstract class GeoAbstractGraphQLService<T, ID extends Serializable> {

	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;

	protected final GeoRepository<T, ID> repository;
	protected RSQLParser rsqlParser;
	private static final Logger logger = LoggerFactory.getLogger(GeoAbstractGraphQLService.class);
	private final Class<T> clazz;

	public GeoAbstractGraphQLService(GeoRepository<T, ID> repository, final Class<T> clazz) {
		this.repository = repository;
		this.clazz = clazz;
	}

	protected RelayPage<T> getPage(final String search, Pageable pageable, final ResolutionEnvironment env)
	{
		pageable = (pageable == null) ? PageRequest.of(0, 20) : pageable;

		val tSpecification = (StringUtils.hasText(search)) ? this.parseSearch(search) : null;
		val page = this.repository.findAllWithPagination(tSpecification, pageable, this.clazz, this.parseSelect(env));

		return PageFactory.fromPage(page);
	}

	protected List<T> getAll(final String search)
	{
		val tSpecification = (StringUtils.hasText(search)) ?
			this.parseSearch(search) : null;

		return this.repository.findAll(tSpecification);
	}

	private List<String> parseSelect(final ResolutionEnvironment env)
	{
		return this.parseSelect(env, "edges/node/**");
	}

	private List<String> parseSelect(final ResolutionEnvironment env, final String search)
	{
		List<String> result = new ArrayList<>();

		env.dataFetchingEnvironment
			.getSelectionSet()
			.getFields(search)
			.forEach(s -> result.add(s.getQualifiedName().replace("edges/node/", "").replace("/", ".")));

		return result;
	}

	/**
   * Return the number of entities matching search
   * @param search RSQL filter
   */
  public long count(final String search) {

    Specification<T> spec = null;

    if(StringUtils.hasText(search)) {
      spec = Specification.where(this.parseSearch(search));
    }

    return this.repository.count(spec);
  }

	/**
	 * Get one entity by this id.
	 *
	 * @param id Entity id value.
	 * @return Entity optional.
	 */
	protected Optional<T> getOne(ID id) {
		return this.repository.findById(id);
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
		String[] ignoredProps = Arrays.stream(pds)
				.filter(p -> {
					try {
						return src.getPropertyValue(p.getName()) == null;
					} catch(Exception ex) {
						return true;
					}
				})
				.map(FeatureDescriptor::getName)
				.toArray(String[]::new);

		BeanUtils.copyProperties(from, to, ignoredProps);

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

	/**
	 * Calcul aggregations from entities list
	 * @deprecated Wrong implementation, need to be reimplemented by using queries/predicates
	 * @param source Entities list
	 * @param summaries Requested aggregations
	 * @return Computed summaries
	 */
	public static <T> List<Double> summarize(List<T> source, List<Summary> summaries) {
    return summaries.stream()
    .map(s -> {
      return source.stream()
      .filter(entity -> {
        Class<?> clazz = entity.getClass(); // ? Generic to Handle projections
				String getter = "get" + s.getSelector().substring(0, 1).toUpperCase() + s.getSelector().substring(1);
        try {
          clazz.getMethod(getter);
          return true;
        } catch(Exception e) {
					GeoAbstractGraphQLService.logger.info("Summarize operation error, continuing" + e.getMessage());
          return false;
        }
      })
      .map(entity -> {
        Class<?> clazz = entity.getClass(); // ? Generic to Handle projections
				String getter = "get" + s.getSelector().substring(0, 1).toUpperCase() + s.getSelector().substring(1);
        try {
					Method method = clazz.getMethod(getter);
					Object value = method.invoke(entity);
					if(value == null) return 0d;
					if (value.getClass().equals(Integer.class))
						return Double.valueOf((Integer)value);
					if (value.getClass().equals(Float.class))
						return Double.valueOf((Float)value);
					return (Double) value;
        } catch(Exception e) {
          throw new RuntimeException(e);
        }
      })
      .reduce(0d, (subtotal, element) -> {
				if(s.getSummaryType() == SummaryType.SUM)
					return subtotal + element;
				else return null;
				// else throw new RuntimeException("Summary type not implemented: " + s.getSummaryType());
			});
    })
    .collect(Collectors.toList());
	}

}
