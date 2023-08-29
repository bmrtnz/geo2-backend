package fr.microtec.geo2.service.graphql.tiers;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.tiers.GeoTypePalette;
import fr.microtec.geo2.persistance.repository.tiers.GeoTypePaletteRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoTypePaletteGraphQLService extends GeoAbstractGraphQLService<GeoTypePalette, String> {

    public GeoTypePaletteGraphQLService(GeoTypePaletteRepository repository) {
        super(repository, GeoTypePalette.class);
    }

    @GraphQLQuery
    public RelayPage<GeoTypePalette> allTypePalette(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoTypePalette> getTypePalette(
            @GraphQLArgument(name = "id") String id) {
        return super.getOne(id);
    }

    @GraphQLQuery
    public BigDecimal fetchNombreColisParPalette(
            String typePalette,
            String article,
            String secteur) {
        return ((GeoTypePaletteRepository) this.repository)
                .fetchNombreColisParPalette(typePalette, article, secteur);
    }

}
