package fr.microtec.geo2.service.common;

import fr.microtec.geo2.persistance.EntityUtils;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
@GraphQLApi
public class GeoExistsGraphQlService {

	@PersistenceContext
	private EntityManager entityManager;

	@GraphQLQuery
	public boolean exists(
			@GraphQLArgument(name = "type") String inputType,
			@GraphQLArgument(name = "field") String requestedField,
			@GraphQLArgument(name = "value") String value
	) {
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = criteriaBuilder.createQuery(Integer.class);
		Root<?> root = criteriaQuery.from(EntityUtils.getEntityClassFromName(inputType));

		criteriaQuery
				.select(criteriaBuilder.literal(1))
				.where(criteriaBuilder.equal(root.get(requestedField), value));

		TypedQuery<Integer> query = this.entityManager.createQuery(criteriaQuery);
		List<Integer> result = query.getResultList();

		if (result.isEmpty()) {
			return false;
		}

		return result.get(0) > 0;
	}

}
