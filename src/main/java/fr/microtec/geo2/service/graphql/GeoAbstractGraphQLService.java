package fr.microtec.geo2.service.graphql;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.hibernate.Hibernate;
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
import org.springframework.util.StringUtils;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import fr.microtec.geo2.common.CustomUtils;
import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.configuration.graphql.Summary;
import fr.microtec.geo2.persistance.repository.GeoRepository;
import fr.microtec.geo2.persistance.rsql.GeoCustomVisitor;
import io.leangen.graphql.execution.ResolutionEnvironment;
import lombok.val;

/**
 * Abstract Geo graphQl service.
 *
 * @param <T>  Entity type.
 * @param <ID> Entity id type.
 */
public abstract class GeoAbstractGraphQLService<T, ID extends Serializable> {

    @PersistenceUnit
    protected EntityManagerFactory entityManagerFactory;

    protected final GeoRepository<T, ID> repository;
    protected RSQLParser rsqlParser;
    private final Class<T> clazz;

    public GeoAbstractGraphQLService(GeoRepository<T, ID> repository, final Class<T> clazz) {
        this.repository = repository;
        this.clazz = clazz;
    }

    private Page<T> fetchPage(final String search, Pageable pageable, final Set<String> fields) {
        Specification<T> spec = (StringUtils.hasText(search)) ? this.parseSearch(search) : null;
        return this.fetchPage(spec, pageable, fields);
    }

    private Page<T> fetchPage(final Specification<T> spec, Pageable pageable, final Set<String> fields) {
        pageable = (pageable == null) ? PageRequest.of(0, 20) : pageable;
        Page<T> page = this.repository.findAllWithPagination(spec, pageable, this.clazz,
                CustomUtils.parseSelect(fields));

        return page;
    }

    protected RelayPage<T> getPage(final String search, Pageable pageable, final Set<String> fields) {
        val page = this.fetchPage(search, pageable, CustomUtils.parseSelect(fields));
        return PageFactory.asRelayPage(page);
    }

    protected RelayPage<T> getPage(final Specification<T> spec, Pageable pageable, final Set<String> fields) {
        val page = this.fetchPage(spec, pageable, CustomUtils.parseSelect(fields));
        return PageFactory.asRelayPage(page);
    }

    protected RelayPage<T> getPage(final String search, Pageable pageable, final Set<String> fields,
            Function<List<Summary>, List<Double>> summaryResolver) {
        val page = this.fetchPage(search, pageable, CustomUtils.parseSelect(fields));
        return PageFactory.asRelayPage(page, summaryResolver);
    }

    protected RelayPage<T> getPage(final Specification<T> spec, Pageable pageable, final Set<String> fields,
            Function<List<Summary>, List<Double>> summaryResolver) {
        val page = this.fetchPage(spec, pageable, CustomUtils.parseSelect(fields));
        return PageFactory.asRelayPage(page, summaryResolver);
    }

    /**
     * @deprecated Use alternative signature in combinaison with
     *             `@GraphQLEnvironment() final Set<String> fields` instead
     * @see fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService#getPage(String,Pageable,Set<String>)
     */
    @Deprecated
    protected RelayPage<T> getPage(final String search, Pageable pageable, final ResolutionEnvironment env) {
        return this.getPage(search, pageable, CustomUtils.parseSelectFromPagedEnv(env));
    }

    protected List<T> getUnpaged(final String search, final ResolutionEnvironment env) {
        Specification<T> spec = (StringUtils.hasText(search)) ? this.parseSearch(search) : null;
        Set<String> fields = CustomUtils.parseSelectFromEnv(env);
        return this.repository.findAllWithPaginations(spec, Pageable.unpaged(), this.clazz, fields);
    }

    /**
     * @deprecated This implementation will fetch all fields of the entity
     *             Use `{@link getUnpaged}` instead
     */
    @Deprecated
    protected List<T> getAll(final String search) {
        val tSpecification = (StringUtils.hasText(search)) ? this.parseSearch(search) : null;
        return this.repository.findAll(tSpecification);
    }

    /**
     * Return the number of entities matching search
     *
     * @param search RSQL filter
     */
    public long count(final String search) {

        Specification<T> spec = null;

        if (StringUtils.hasText(search)) {
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
     * @param to   Destination entity.
     * @param env  GraphQL environment.
     * @return Merged entity data.
     */
    public static <T> T merge(T from, T to, Map<String, Object> graphQlArguments) {
        List<String> nullArgumentsName = graphQlArguments != null ? graphQlArguments
                .entrySet()
                .stream()
                .filter(e -> e.getValue() == null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()) : List.of();

        BeanWrapper src = new BeanWrapperImpl(from);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        String[] ignoredProps = Arrays.stream(pds)
                .filter(p -> !nullArgumentsName.contains(p.getName()))
                .filter(p -> {
                    try {
                        return src.getPropertyValue(p.getName()) == null;
                    } catch (Exception ex) {
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
    protected T save(T data, Map<String, Object> graphQlArguments) {
        ID id = (ID) this.getId(data);

        if (id != null) {
            Optional<T> optionalEntity = this.repository.findById(id);

            if (optionalEntity.isPresent()) {
                data = this.merge(data, optionalEntity.get(), graphQlArguments);
            }
        }

        return this.repository.save(data);
    }

    protected List<T> saveAll(final List<T> data, List<Map<String, Object>> graphQlArguments) {
        return this.repository.saveAll(data.stream()
                .map(entity -> {
                    T res = entity;
                    ID id = (ID) this.getId(entity);

                    if (id != null) {
                        T optionalEntity = Hibernate.unproxy(this.repository.getOne(id), this.clazz);

                        if (optionalEntity != null) {
                            res = GeoAbstractGraphQLService.merge(entity, optionalEntity,
                                    graphQlArguments.get(data.indexOf(entity)));
                        }
                    }
                    return res;
                })
                .collect(Collectors.toList()));
    }

    protected T saveEntity(T data, ResolutionEnvironment env) {
        Map<String, Object> parsedArguments = CustomUtils.parseArgumentFromEnv(env, this.clazz);
        return this.save(data, parsedArguments);
    }

    protected List<T> saveAllEntities(List<T> data, ResolutionEnvironment env) {
        List<Map<String, Object>> parsedArguments = CustomUtils.parseArgumentFromEnv(env, this.clazz, "all");
        return this.saveAll(data, parsedArguments);
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

        if (entityPersister.canExtractIdOutOfEntity()) {
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
