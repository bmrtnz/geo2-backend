package fr.microtec.geo2.service;

import java.util.List;
import java.util.stream.IntStream;

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
    .getTotauxDetailList(ordre);

    return PageFactory.fromRelayPage(PageFactory.fromPage(page),summarize(all, summaries));
  }

  public SummarisedRelayPage<GeoOrdreLigne> fetchAllSummarized(String search,Pageable pageable,List<Summary> summaries) {
    Page<GeoOrdreLigne> page = this.ordreLigneRepository
    .findAll(parseSearch(search),pageable);

    List<GeoOrdreLigne> all = this.ordreLigneRepository
    .findAll(parseSearch(search),pageable.getSort());

    List<Double> summary = summarize(all, summaries);

    // Custom summaries
    IntStream.range(0, summaries.size())
    .forEach(index -> {
      Summary s = summaries.get(index);
      if(s.getSelector().equals("pourcentageMargeBrute")) {
        summary.add(index,this.fetchTotalMargeBrute(summaries,summary) / this.getSummaryResult(summaries, summary, "totalVenteBrut"));
      }
      if(s.getSelector().equals("pourcentageMargeNette")) {
        Double totalMargeBrute = this.fetchTotalMargeBrute(summaries,summary);
        Double totalObjectifMarge = this.getSummaryResult(summaries, summary, "totalObjectifMarge");
        Double totalVenteBrute = this.getSummaryResult(summaries, summary, "totalVenteBrut");
        summary.add(index,(totalMargeBrute - totalObjectifMarge) / totalVenteBrute);
      }
    });

    return PageFactory.fromRelayPage(PageFactory.fromPage(page), summary);
  }

  private Double getSummaryResult(List<Summary> summaries,List<Double> summary,String field){
    return summary.get(summaries.indexOf(summaries.stream().filter(sl -> sl.getSelector().equals(field)).findFirst().get()));
  };

  private Double fetchTotalMargeBrute(List<Summary> summaries,List<Double> summary){
    Double totalVenteBrut = this
    .getSummaryResult(summaries,summary,"totalVenteBrut");
    Double totalRemise = this
    .getSummaryResult(summaries,summary,"totalRemise");
    Double totalRestitue = this
    .getSummaryResult(summaries,summary,"totalRestitue");
    Double totalFraisMarketing = this
    .getSummaryResult(summaries,summary,"totalFraisMarketing");
    Double totalAchat = this
    .getSummaryResult(summaries,summary,"totalAchat");
    Double totalTransport = this
    .getSummaryResult(summaries,summary,"totalTransport");
    Double totalTransit = this
    .getSummaryResult(summaries,summary,"totalTransit");
    Double totalCourtage = this
    .getSummaryResult(summaries,summary,"totalCourtage");
    Double totalFraisAdditionnels = this
    .getSummaryResult(summaries,summary,"totalFraisAdditionnels");
    return totalVenteBrut - totalRemise+ totalRestitue - totalFraisMarketing - totalAchat - totalTransport - totalTransit - totalCourtage - totalFraisAdditionnels;
  };

}
