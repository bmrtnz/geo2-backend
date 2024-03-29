package fr.microtec.geo2.service.graphql.ordres;

import java.util.Optional;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import io.leangen.graphql.annotations.*;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.ordres.GeoCommentaireOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoCommentaireOrdre;
import fr.microtec.geo2.persistance.repository.ordres.GeoCommentaireOrdreRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoCommentaireOrdreGraphQLService extends GeoAbstractGraphQLService<GeoCommentaireOrdre, String> {

	public GeoCommentaireOrdreGraphQLService(GeoCommentaireOrdreRepository repository) {
		super(repository, GeoCommentaireOrdre.class);
	}

	@GraphQLQuery
	public RelayPage<GeoCommentaireOrdre> allCommentaireOrdre(
			@GraphQLArgument(name = "search") String search,
			@GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
			@GraphQLEnvironment ResolutionEnvironment env
	) {
		return this.getPage(search, pageable, env);
	}

	@GraphQLQuery
	public Optional<GeoCommentaireOrdre> getCommentaireOrdre(
			@GraphQLArgument(name = "id") String id
	) {
		return super.getOne(id);
    }

    @GraphQLMutation
    public GeoCommentaireOrdre saveCommentaireOrdre(GeoCommentaireOrdre commentaireOrdre, @GraphQLEnvironment ResolutionEnvironment env) {
        return this.saveEntity(commentaireOrdre, env);
    }
}
