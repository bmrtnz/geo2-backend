package fr.microtec.geo2.service.common;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.Distinct;
import fr.microtec.geo2.persistance.EntityUtils;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Service
@GraphQLApi
public class GeoDistinctGraphQLService {

	private static final String SQL_DISTINCT = "SELECT DISTINCT %s AS key, COUNT(%s) AS count FROM %s%s GROUP BY %s";
	private static final String SQL_COUNT = "SELECT COUNT(DISTINCT %s) FROM %s%s";
	private static final String SQL_WHERE = " WHERE UPPER(%s) LIKE :like";

	private final EntityManagerFactory entityManagerFactory;

	public GeoDistinctGraphQLService(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@GraphQLQuery
	public RelayPage<Distinct> getDistinct(
			@GraphQLArgument(name = "type") String inputType,
			@GraphQLArgument(name = "field") String requestField,
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable
	) {
		Class<?> entityClass = EntityUtils.getEntityClassFromName(inputType);
		MetamodelImplementor metamodel = (MetamodelImplementor) this.entityManagerFactory.getMetamodel();
		AbstractEntityPersister entityPersister = (AbstractEntityPersister) metamodel.entityPersister(entityClass);

		String tableName = entityPersister.getTableName();
		String idField = entityPersister.getIdentifierPropertyName();
		String columnId = EntityUtils.getEntityPropertyColumnName(entityClass, idField);
		String columnName = EntityUtils.getEntityPropertyColumnName(entityClass, requestField);

		return this.executeQuery(tableName, columnId, columnName, search, pageable);
	}

	/**
	 * Execute distinct search query.
	 *
	 * @param tableName The table name.
	 * @param columnId The id column name.
	 * @param columnName The distinct column name.
	 * @param search Search filter, can be null or blank.
	 * @param pageable Pageable params.
	 * @return Query result.
	 */
	private RelayPage<Distinct> executeQuery(String tableName, String columnId, String columnName, String search, Pageable pageable) {
		EntityManager entityManager = this.entityManagerFactory.createEntityManager();

		boolean hasWhere = search != null && !search.isBlank();
		String sqlWherePart = "";
		String sqlWhereValue = "";
		if (hasWhere) {
			sqlWherePart = String.format(SQL_WHERE, columnName);
			sqlWhereValue = String.format("*%s*", search.toUpperCase());
		}

		String sql = String.format(SQL_DISTINCT, columnName, columnId, tableName, sqlWherePart, columnName);
		String countSql = String.format(SQL_COUNT, columnName, tableName, sqlWherePart);

		Query query = entityManager.createNativeQuery(sql, Distinct.class);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Query countQuery = entityManager.createNativeQuery(countSql);

		if (hasWhere) {
			query.setParameter("like", sqlWhereValue);
			countQuery.setParameter("like", sqlWhereValue);
		}

		BigDecimal totalCount = (BigDecimal) countQuery.getSingleResult();
		List<Distinct> result = (List<Distinct>) query.getResultList();

		return PageFactory.fromPage(new PageImpl<>(result, pageable, totalCount.intValue()));
	}

}
