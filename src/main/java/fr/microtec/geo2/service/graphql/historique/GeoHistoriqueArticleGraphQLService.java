package fr.microtec.geo2.service.graphql.historique;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.historique.GeoHistoriqueArticle;
import fr.microtec.geo2.persistance.repository.historique.GeoHistoriqueArticleRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
public class GeoHistoriqueArticleGraphQLService extends GeoAbstractGraphQLService<GeoHistoriqueArticle, String> {

	public GeoHistoriqueArticleGraphQLService(GeoHistoriqueArticleRepository repository) {
		super(repository);
	}

	@GraphQLMutation
	public GeoHistoriqueArticle saveHistoriqueArticle(GeoHistoriqueArticle historiqueArticle) {
		return this.save(historiqueArticle);
	}

}