package fr.microtec.geo2.service.graphql.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import cz.jirutka.rsql.parser.RSQLParser;
import fr.microtec.geo2.common.CustomUtils;
import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.EntityUtils;
import fr.microtec.geo2.persistance.StringEnum;
import fr.microtec.geo2.persistance.entity.Distinct;
import fr.microtec.geo2.persistance.rsql.GeoCustomVisitor;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoDistinctGraphQLService {

    private final EntityManager entityManager;
    private final RSQLParser rsqlParser;

    public GeoDistinctGraphQLService(EntityManager entityManager, RSQLParser rsqlParser) {
        this.entityManager = entityManager;
        this.rsqlParser = rsqlParser;
    }

    @GraphQLQuery
    public RelayPage<Distinct> getDistinct(
            @GraphQLArgument(name = "type") String inputType,
            @GraphQLArgument(name = "field") String requestField,
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable) {
        Specification<?> spec = null;
        if (search != null && !search.isBlank()) {
            spec = this.rsqlParser.parse(search).accept(new GeoCustomVisitor<>());
        }

        Page<Distinct> result = this.readPage(spec, pageable, EntityUtils.getEntityClassFromName(inputType),
                requestField);

        return PageFactory.asRelayPage(result);
    }

    /**
     * Read page with criteria query.
     * Query select distinct {requestedField} from associated table on {inputType}
     * entity.
     * Apply specification if present and paging parameters.
     *
     * @param spec           Specification to apply.
     * @param pageable       Pageable parameter.
     * @param entityClass    Entity class.
     * @param requestedField Entity requested field.
     * @return Request page data.
     */
    private Page<Distinct> readPage(
            Specification<?> spec, Pageable pageable,
            Class<?> entityClass, String requestedField) {

        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Distinct> criteriaQuery = CriteriaUtils.selectCountDistinct(
                criteriaBuilder, entityClass, requestedField, spec);

        // Handle enum types
        Optional<Field> field = CustomUtils.getField(entityClass, requestedField);
        if (field.isPresent())
            if (StringEnum.class.isAssignableFrom(field.get().getType()))
                return readEnumPage(field.get().getType(), pageable);

        // Order
        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        if (sort.isSorted()) {
            criteriaQuery.orderBy(
                    CriteriaUtils.toOrders(sort, CriteriaUtils.findRoot(criteriaQuery, entityClass), criteriaBuilder));
        }

        TypedQuery<Distinct> query = this.entityManager.createQuery(criteriaQuery);

        // Paging
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        CriteriaQuery<Long> countCriteriaQuery = CriteriaUtils.countDistinct(criteriaBuilder, entityClass,
                requestedField, spec);
        TypedQuery<Long> countQuery = this.entityManager.createQuery(countCriteriaQuery);

        return PageableExecutionUtils.getPage(
                query.getResultList(),
                pageable,
                () -> CustomUtils.executeCountQuery(countQuery));
    }

    public Page<Distinct> readEnumPage(Class<?> type, Pageable pageable) {
        Class<?>[] interfaces = type.getInterfaces();
        Page<Distinct> page = Page.empty();

        for (Class<?> inter : interfaces) {
            if (StringEnum.class.equals(inter)) {
                try {
                    List<String> keys = (List<String>) inter
                            .getMethod("getKeys", Class.class)
                            .invoke(null, type);
                    List<Distinct> result = keys.stream()
                            .map(k -> new Distinct(k, -1l))
                            .collect(Collectors.toList());
                    page = PageableExecutionUtils.getPage(result, pageable, () -> -1);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    throw new RuntimeException(String.format("Unable to load Page of distinct '%s'", type));
                }
            }
        }

        return page;
    }

}
