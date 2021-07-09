package fr.microtec.geo2.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.SummarisedRelayPage;
import fr.microtec.geo2.configuration.graphql.Summary;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigneTotauxDetail;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service()
public class OrdreLigneService extends GeoAbstractGraphQLService<GeoOrdreLigne, String> {
  
  private final GeoOrdreLigneRepository ordreLigneRepository;
  private final GeoOrdreRepository ordreRepository;

  public OrdreLigneService(
    GeoOrdreRepository ordreRepository,
    GeoOrdreLigneRepository ordreLigneRepository
  ) {
    super(ordreLigneRepository);
    this.ordreRepository = ordreRepository;
    this.ordreLigneRepository = ordreLigneRepository;
  }

  public SummarisedRelayPage<GeoOrdreLigneTotauxDetail> fetchOrdreLignesTotauxDetail(String ordreID,Pageable pageable,List<Summary> summaries) {
    // reset sorting
    Pageable pageableUnsorted = PageRequest
    .of(pageable.getPageNumber(), pageable.getPageSize());
    GeoOrdre ordre = this.ordreRepository.getOne(ordreID);
    Page<GeoOrdreLigneTotauxDetail> page = this.ordreLigneRepository
    .getTotauxDetail(ordre,pageableUnsorted);

    List<GeoOrdreLigneTotauxDetail> all = this.ordreLigneRepository
    .getTotauxDetailList(ordre,pageableUnsorted);

    return PageFactory.fromRelayPage(PageFactory.fromPage(page),summarize(all, summaries));
  }

}
