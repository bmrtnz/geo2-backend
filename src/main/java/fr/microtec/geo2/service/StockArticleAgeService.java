package fr.microtec.geo2.service;

import java.util.List;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.stock.GeoStock;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;

@Service
public class StockArticleAgeService {

    public static Specification<GeoStockArticleAge> withDistinctArticleInOrdreLigne() {
        return (root, criteriaQuery, criteriaBuilder) -> {

            Subquery<GeoOrdreLigne> subqueryOL = criteriaQuery.subquery(GeoOrdreLigne.class);
            Root<GeoOrdreLigne> rootOL = subqueryOL.from(GeoOrdreLigne.class);

            subqueryOL.select(rootOL.get("article")).distinct(true);

            return criteriaBuilder.in(root.get("article")).value(subqueryOL);

        };
    }

    public static Specification<GeoStockArticleAge> withArticleInSecteurs(List<GeoSecteur> secteurs) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            Subquery<GeoOrdreLigne> subqueryOL = criteriaQuery.subquery(GeoOrdreLigne.class);
            Root<GeoOrdreLigne> rootOL = subqueryOL.from(GeoOrdreLigne.class);

            subqueryOL.select(rootOL.get("article")).where(rootOL.get("ordre").get("secteurCommercial").in(secteurs));

            return criteriaBuilder.in(root.get("article")).value(subqueryOL);
        };
    }

    public static Specification<GeoStockArticleAge> withArticleInClients(List<GeoClient> clients) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            Subquery<GeoOrdreLigne> subqueryOL = criteriaQuery.subquery(GeoOrdreLigne.class);
            Root<GeoOrdreLigne> rootOL = subqueryOL.from(GeoOrdreLigne.class);

            subqueryOL.select(rootOL.get("article")).where(rootOL.get("ordre").get("client").in(clients));

            return criteriaBuilder.in(root.get("article")).value(subqueryOL);
        };
    }

    public static Specification<GeoStockArticleAge> withArticleInFournisseurs(List<GeoFournisseur> fournisseurs) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            Subquery<GeoStock> subqueryStock = criteriaQuery.subquery(GeoStock.class);
            Root<GeoStock> rootStock = subqueryStock.from(GeoStock.class);

            subqueryStock.select(rootStock.get("article")).where(rootStock.get("fournisseur").in(fournisseurs));

            return criteriaBuilder.in(root.get("article")).value(subqueryStock);
        };
    }

    public static Specification<GeoStockArticleAge> withArticleInSociete(GeoSociete societe) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            Subquery<GeoOrdreLigne> subqueryOL = criteriaQuery.subquery(GeoOrdreLigne.class);
            Root<GeoOrdreLigne> rootOL = subqueryOL.from(GeoOrdreLigne.class);

            subqueryOL.select(rootOL.get("article"))
                    .where(criteriaBuilder.equal(rootOL.get("ordre").get("societe"), societe));

            return criteriaBuilder.in(root.get("article")).value(subqueryOL);
        };
    }

}
