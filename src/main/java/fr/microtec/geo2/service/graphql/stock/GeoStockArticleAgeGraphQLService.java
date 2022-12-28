package fr.microtec.geo2.service.graphql.stock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.produits.GeoEmballage;
import fr.microtec.geo2.persistance.entity.produits.GeoOrigine;
import fr.microtec.geo2.persistance.entity.produits.GeoVariete;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAgeKey;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.stock.GeoStockArticleAgeRepository;
import fr.microtec.geo2.service.StockArticleAgeService;
import fr.microtec.geo2.service.StockService;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoStockArticleAgeGraphQLService
        extends GeoAbstractGraphQLService<GeoStockArticleAge, GeoStockArticleAgeKey> {

    private final StockService stockService;
    private final StockArticleAgeService service;

    public GeoStockArticleAgeGraphQLService(
            GeoStockArticleAgeRepository repository,
            StockService stockService,
            StockArticleAgeService service) {
        super(repository, GeoStockArticleAge.class);
        this.stockService = stockService;
        this.service = service;
    }

    @GraphQLQuery
    public RelayPage<GeoStockArticleAge> allStockArticleAge(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public RelayPage<GeoStockArticleAge> fetchStock(
            @GraphQLArgument(name = "societe") GeoSociete societe,
            @GraphQLArgument(name = "secteurs") List<GeoSecteur> secteurs,
            @GraphQLArgument(name = "clients") List<GeoClient> clients,
            @GraphQLArgument(name = "fournisseurs") List<GeoFournisseur> fournisseurs,
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.stockService.fetchStockArticleAge(societe, secteurs, clients, fournisseurs, search, pageable);
    }

    @GraphQLQuery
    public Optional<GeoStockArticleAge> getStockArticleAge(
            @GraphQLArgument(name = "id") GeoStockArticleAgeKey id,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return super.getOne(id);
    }

    @GraphQLQuery
    public List<GeoVariete> subDistinctStockVariete(String especeID) {
        return this.service.subDistinct(especeID, GeoVariete.class);
    }

    @GraphQLQuery
    public List<GeoOrigine> subDistinctStockOrigine(String especeID) {
        return this.service.subDistinct(especeID, GeoOrigine.class);
    }

    @GraphQLQuery
    public List<GeoEmballage> subDistinctStockEmballage(String especeID) {
        return this.service.subDistinct(especeID, GeoEmballage.class);
    }

}
