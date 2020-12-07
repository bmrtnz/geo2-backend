package fr.microtec.geo2.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAgeKey;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAgeSpecifications;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.stock.GeoStockArticleAgeRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service()
public class StockService extends GeoAbstractGraphQLService<GeoStockArticleAge, GeoStockArticleAgeKey> {

  private final GeoStockArticleAgeRepository stockArticleAgeRepository;

  public StockService(GeoStockArticleAgeRepository stockArticleAgeRepository) {
    super(stockArticleAgeRepository);
    this.stockArticleAgeRepository = stockArticleAgeRepository;
  }

  public RelayPage<GeoStockArticleAge> fetchStockArticleAge(GeoSociete societe, List<GeoSecteur> secteurs,
                    List<GeoClient> clients, List<GeoFournisseur> fournisseurs, String search, Pageable pageable) {
    Page<GeoStockArticleAge> page;

    if (pageable == null)
      pageable = PageRequest.of(0, 20);

    Specification<GeoStockArticleAge> spec = GeoStockArticleAgeSpecifications.withDistinctArticleInOrdreLigne();

    if (societe != null)
      spec = spec.and(GeoStockArticleAgeSpecifications.withArticleInSociete(societe));

    if (secteurs != null)
      spec = spec.and(GeoStockArticleAgeSpecifications.withArticleInSecteurs(secteurs));

    if (clients != null)
      spec = spec.and(GeoStockArticleAgeSpecifications.withArticleInClients(clients));

    if (fournisseurs != null)
      spec = spec.and(GeoStockArticleAgeSpecifications.withArticleInFournisseurs(fournisseurs));

    if (search != null && !search.isBlank())
      spec = spec.and(this.parseSearch(search));

    page = this.stockArticleAgeRepository.findAll(spec, pageable); //, GeoEntityGraph.getEntityGraph(env));

    return PageFactory.fromPage(page);
  }

}
