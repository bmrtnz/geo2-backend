package fr.microtec.geo2.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.common.CustomUtils;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.configuration.graphql.Summary;
import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import graphql.GraphQLException;
import io.leangen.graphql.execution.ResolutionEnvironment;
import lombok.val;

@Service()
public class OrdreLigneService extends GeoAbstractGraphQLService<GeoOrdreLigne, String> {
  
  private final EntityManager entityManager;
  
  public OrdreLigneService(
    GeoOrdreLigneRepository ordreLigneRepository,
    EntityManager entityManager
  ) {
    super(ordreLigneRepository, GeoOrdreLigne.class);
    this.entityManager = entityManager;
  }

  public RelayPage<GeoOrdreLigne> fetchOrdreLignesTotauxDetail(String search, Pageable pageable, final ResolutionEnvironment env) {

    Set<String> fields = CustomUtils.parseSelectFromEnv(env);
    Specification<GeoOrdreLigne> spec = ((Specification<GeoOrdreLigne>)CriteriaUtils.groupedBySelection(fields));

    if(search != null && !search.isBlank())
      spec = spec.and(this.parseSearch(search));

    return this.getPage(search, pageable, fields, summaries -> {
      CriteriaQuery<?> query = CriteriaUtils.createSummariesQuery(
        this.entityManager.getCriteriaBuilder(),
        GeoOrdreLigne.class,
        this.parseSearch(search),
        fields,
        summaries
      );

      TypedQuery<Object[]> qt = (TypedQuery<Object[]>) this.entityManager.createQuery(query);
      List<Double> result = new ArrayList(Arrays.asList(qt.getSingleResult()));

      return result;
    });
  }

  public RelayPage<GeoOrdreLigne> fetchAllMarge(String search,Pageable pageable, final ResolutionEnvironment env) {

    Set<String> fields = CustomUtils.parseSelectFromEnv(env);

    return this.getPage(search, pageable, fields, summaries -> {
      CriteriaQuery<?> query = CriteriaUtils.createSummariesQuery(
        this.entityManager.getCriteriaBuilder(),
        GeoOrdreLigne.class,
        this.parseSearch(search),
        fields,
        summaries
      );

      TypedQuery<Object[]> qt = (TypedQuery<Object[]>) this.entityManager.createQuery(query);
      List<Double> result = new ArrayList(Arrays.asList(qt.getSingleResult()));
      Function<String,Boolean> hasSelector = selector -> summaries
      .stream()
      .anyMatch( s -> s.getSelector().equals(selector));
      Function<String,Integer> getIndex = selector -> summaries
      .stream()
      .map(s -> s.getSelector())
      .collect(Collectors.toList())
      .indexOf(selector);

      // handling special cases
      if(hasSelector.apply("margeBrute")) {
        val res = this.fetchTotalMargeBrute(summaries, result);
        result.add(getIndex.apply("margeBrute"), res);
      }
      
      if(hasSelector.apply("pourcentageMargeBrute")) {
        val res = this.fetchTotalMargeBrute(summaries,result) / this.getSummaryResult(summaries, result, "totalVenteBrut");
        result.add(getIndex.apply("pourcentageMargeBrute"), res);
      }
    
      if(hasSelector.apply("pourcentageMargeNette")) {
        Double totalMargeBrute = this.fetchTotalMargeBrute(summaries,result);
        Double totalObjectifMarge = this.getSummaryResult(summaries, result, "totalObjectifMarge");
        Double totalVenteBrute = this.getSummaryResult(summaries, result, "totalVenteBrut");
        result.add(getIndex.apply("pourcentageMargeNette"), (totalMargeBrute - totalObjectifMarge) / totalVenteBrute);
      }

      return result;
    });
  }

  private Double getSummaryResult(List<Summary> summaries,List<Double> summary,String field){
    val item = summaries
    .stream()
    .filter(sl -> sl.getSelector().equals(field))
    .findFirst();

    if(item.isEmpty())
      throw new GraphQLException("Missing mandatory field :" + field);

    return summary.get(summaries.indexOf(item.get()));
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
    return totalVenteBrut - totalRemise + totalRestitue - totalFraisMarketing - totalAchat - totalTransport - totalTransit - totalCourtage - totalFraisAdditionnels;
  };

}
