package fr.microtec.geo2.service.graphql.common;

import ch.qos.logback.core.db.dialect.OracleDialect;
import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.EntityUtils;
import graphql.GraphQLException;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@GraphQLApi
public class GeoLocatePageGraphQlService {

	private static final String ORACLE_ROOT_LOCATE_SQL = "SELECT numrow FROM (%s) WHERE %s";
	private static final String ORACLE_LOCATE_SQL = "SELECT %s, rownum as numrow FROM %s";

	@PersistenceContext
	private EntityManager entityManager;

	@GraphQLQuery
	public Long locatePage(
			@GraphQLArgument(name = "type") String inputType,
			@GraphQLArgument(name = "key") String[] keyValues,
			@GraphQLArgument(name = "pageSize") Integer pageSize
	) {
		Session session = (Session) this.entityManager.getDelegate();
		SessionFactoryImpl sessionFactory = (SessionFactoryImpl) session.getSessionFactory();
		Dialect dialect = sessionFactory.getJdbcServices().getDialect();

		Class<?> entityClass = EntityUtils.getEntityClassFromName(inputType);
		ClassMetadata metadata = sessionFactory.getClassMetadata(entityClass);
		AbstractEntityPersister entityPersister = (AbstractEntityPersister) metadata;

		if (entityPersister.getKeyColumnNames().length != keyValues.length) {
			throw new GraphQLException(String.format(
					"missing key value (required : %d - found : %d)",
					entityPersister.getKeyColumnNames().length,
					keyValues.length
			));
		}

		String pkFieldsList = String.join(", ", entityPersister.getKeyColumnNames());
		String pkWhereClause = Arrays.stream(entityPersister.getKeyColumnNames())
				.map(f -> f.concat(" = ?"))
				.collect(Collectors.joining(", "));

		String sql = "";
		if (dialect instanceof Oracle8iDialect) { // Oracle8iDialect is root level oracle dialect
			String fromSql = String.format(ORACLE_LOCATE_SQL, pkFieldsList, entityPersister.getTableName());
			sql = String.format(ORACLE_ROOT_LOCATE_SQL, fromSql, pkWhereClause);
		} else {
			throw new RuntimeException(String.format("Please implement query for %s dialect.", dialect.getClass().getSimpleName()));
		}

		Query query = this.entityManager.createNativeQuery(sql);

		int parameterIndex = 1;
		for (String keyPart : keyValues) {
			query.setParameter(parameterIndex++, keyPart);
		}

		List<BigDecimal> result = (List<BigDecimal>) query.getResultList();
		if (result.isEmpty()) {
			return -1L;
		}

		return Math.floorDiv(result.get(0).longValue() - 1, pageSize);
	}

}
