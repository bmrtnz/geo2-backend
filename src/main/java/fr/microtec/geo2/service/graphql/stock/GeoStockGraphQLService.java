package fr.microtec.geo2.service.graphql.stock;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoStock;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticle;
import fr.microtec.geo2.persistance.repository.stock.GeoStockRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLEnvironment;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;

@Service
@GraphQLApi
@Secured("ROLE_USER")
public class GeoStockGraphQLService extends GeoAbstractGraphQLService<GeoStock, String> {

    public GeoStockGraphQLService(GeoStockRepository stockRepository) {
        super(stockRepository, GeoStock.class);
    }

    @GraphQLQuery
    public RelayPage<GeoStock> allStock(
            @GraphQLArgument(name = "search") String search,
            @GraphQLArgument(name = "pageable") @GraphQLNonNull Pageable pageable,
            @GraphQLEnvironment ResolutionEnvironment env) {
        return this.getPage(search, pageable, env);
    }

    @GraphQLQuery
    public Optional<GeoStock> getStock(
            @GraphQLArgument(name = "id") String id) {
        return this.getOne(id);
    }

    @GraphQLQuery
    public List<GeoStockArticle> allStockArticleList(
            @GraphQLArgument(name = "espece") String espece,
            @GraphQLArgument(name = "variete", defaultValue = "%") String variete,
            @GraphQLArgument(name = "origine", defaultValue = "%") String origine,
            @GraphQLArgument(name = "modeCulture", defaultValue = "%") String modeCulture,
            @GraphQLArgument(name = "emballage", defaultValue = "%") String emballage,
            @GraphQLArgument(name = "bureauAchat", defaultValue = "%") String bureauAchat) {
        return ((GeoStockRepository) this.repository)
                .allStockArticleList(
                        espece,
                        Optional.ofNullable(variete).orElse("%"),
                        Optional.ofNullable(origine).orElse("%"),
                        Optional.ofNullable(modeCulture).orElse("%"),
                        Optional.ofNullable(emballage).orElse("%"),
                        Optional.ofNullable(bureauAchat).orElse("%"));
    }

    @GraphQLQuery
    public Integer quantiteCalculee1(@GraphQLContext GeoStockArticle stockArticle) {
        return stockArticle.getQuantiteInitiale1()
                - stockArticle.getQuantiteReservee1()
                - stockArticle.getQuantiteOptionnelle1();
    }

    @GraphQLQuery
    public Integer quantiteCalculee2(@GraphQLContext GeoStockArticle stockArticle) {
        return stockArticle.getQuantiteInitiale2()
                - stockArticle.getQuantiteReservee2()
                - stockArticle.getQuantiteOptionnelle2();
    }

    @GraphQLQuery
    public Integer quantiteCalculee3(@GraphQLContext GeoStockArticle stockArticle) {
        return stockArticle.getQuantiteInitiale3()
                - stockArticle.getQuantiteReservee3()
                - stockArticle.getQuantiteOptionnelle3();
    }

    @GraphQLQuery
    public Integer quantiteCalculee4(@GraphQLContext GeoStockArticle stockArticle) {
        return stockArticle.getQuantiteInitiale4()
                - stockArticle.getQuantiteReservee4()
                - stockArticle.getQuantiteOptionnelle4();
    }

    @GraphQLQuery
    public String descriptionAbregee(@GraphQLContext GeoStockArticle stockArticle) {
        String value = "";

        if (stockArticle.getStatut() != null && stockArticle.getStatut().equals('O')) {
            value += "-> option ";
            value += stockArticle.getStock().getUtilisateurInfo();
            value += "à";
            value += stockArticle.getStock().getDateInfo().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        Optional<Integer> quantite = stockArticle.getStock().getMouvements().stream()
                .map((mouvement) -> mouvement.getQuantite())
                .reduce((acm, crt) -> acm + crt);

        if (quantite.isPresent())
            if (quantite.get() >= 0) {
                value += " initial=" + stockArticle.getQuantiteInitiale();
                value += " réservé=" + stockArticle.getQuantiteReservee();
            } else
                value += " réappro=" + quantite.get();

        return value;
    }

}
