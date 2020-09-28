package fr.microtec.geo2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.GeoEntityGraph;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAge;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAgeKey;
import fr.microtec.geo2.persistance.entity.stock.GeoStockArticleAgeSpecifications;
import fr.microtec.geo2.persistance.repository.stock.GeoStockArticleAgeRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.execution.ResolutionEnvironment;

@Service()
public class StockService extends GeoAbstractGraphQLService<GeoStockArticleAge, GeoStockArticleAgeKey> {

  private final GeoStockArticleAgeRepository stockArticleAgeRepository;

  public StockService(
    GeoStockArticleAgeRepository stockArticleAgeRepository
  ){
    super(stockArticleAgeRepository);
    this.stockArticleAgeRepository = stockArticleAgeRepository;
  }

  public RelayPage<GeoStockArticleAge> fetchStockArticleAge(
    String search,
    Pageable pageable,
    ResolutionEnvironment env
  ){
    Page<GeoStockArticleAge> page;

		if (pageable == null) {
			pageable = PageRequest.of(0, 20);
		}

		if (search != null && !search.isBlank()) {
      page = this.stockArticleAgeRepository.findAll(this.parseSearch(search), pageable, GeoEntityGraph.getEntityGraph(env));
      // page = this.stockArticleAgeRepository.findAll(this.parseSearch(search).and(GeoStockArticleAgeSpecifications.byDistinctArticleInOrdreLigne()), pageable, GeoEntityGraph.getEntityGraph(env));
      // page = this.stockArticleAgeRepository
      // .findByDistinctArticleInOrdreLigne(
      //   this.parseSearch(search),
      //   pageable,
      //   GeoEntityGraph.getEntityGraph(env)
      // );
		} else {
      page = this.repository.findAll(pageable, GeoEntityGraph.getEntityGraph(env));
      // page = this.stockArticleAgeRepository
      // .findByDistinctArticleInOrdreLigne(null, pageable, GeoEntityGraph.getEntityGraph(env));
		}

		return PageFactory.fromPage(page);
  }

}
