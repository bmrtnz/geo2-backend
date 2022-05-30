package fr.microtec.geo2.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
      EntityManager entityManager) {
    super(ordreLigneRepository, GeoOrdreLigne.class);
    this.entityManager = entityManager;
  }

  public RelayPage<GeoOrdreLigne> fetchOrdreLignesTotauxDetail(String search, Pageable pageable,
      final ResolutionEnvironment env) {

    Set<String> fields = CustomUtils.parseSelectFromEnv(env);
    Specification<GeoOrdreLigne> spec = ((Specification<GeoOrdreLigne>) CriteriaUtils.groupedBySelection(fields));

    if (search != null && !search.isBlank())
      spec = spec.and(this.parseSearch(search));

    return this.getPage(search, pageable, fields, summaries -> {
      CriteriaQuery<?> query = CriteriaUtils.createSummariesQuery(
          this.entityManager.getCriteriaBuilder(),
          GeoOrdreLigne.class,
          this.parseSearch(search),
          fields,
          summaries);

      TypedQuery<Object[]> qt = (TypedQuery<Object[]>) this.entityManager.createQuery(query);
      List<Double> result = new ArrayList(Arrays.asList(qt.getSingleResult()));

      return result;
    });
  }

  public RelayPage<GeoOrdreLigne> fetchAllMarge(String search, Pageable pageable, final ResolutionEnvironment env) {

    Set<String> fields = CustomUtils.parseSelectFromEnv(env);

    return this.getPage(search, pageable, fields, summaries -> {
      CriteriaQuery<?> query = CriteriaUtils.createSummariesQuery(
          this.entityManager.getCriteriaBuilder(),
          GeoOrdreLigne.class,
          this.parseSearch(search),
          fields,
          summaries);

      TypedQuery<Object[]> qt = (TypedQuery<Object[]>) this.entityManager.createQuery(query);
      List<Double> result = new ArrayList(Arrays.asList(qt.getSingleResult()));
      Function<String, Boolean> hasSelector = selector -> summaries
          .stream()
          .anyMatch(s -> s.getSelector().equals(selector));
      Function<String, Integer> getIndex = selector -> summaries
          .stream()
          .map(s -> s.getSelector())
          .collect(Collectors.toList())
          .indexOf(selector);

      // handling special cases
      if (hasSelector.apply("margeBrute")) {
        val res = this.fetchTotalMargeBrute(summaries, result);
        result.add(getIndex.apply("margeBrute"), res);
      }

      if (hasSelector.apply("pourcentageMargeBrute")) {
        val res = this.fetchTotalMargeBrute(summaries, result)
            / this.getSummaryResult(summaries, result, "totalVenteBrut");
        result.add(getIndex.apply("pourcentageMargeBrute"), res);
      }

      if (hasSelector.apply("pourcentageMargeNette")) {
        Double totalMargeBrute = this.fetchTotalMargeBrute(summaries, result);
        Double totalObjectifMarge = this.getSummaryResult(summaries, result, "totalObjectifMarge");
        Double totalVenteBrute = this.getSummaryResult(summaries, result, "totalVenteBrut");
        result.add(getIndex.apply("pourcentageMargeNette"), (totalMargeBrute - totalObjectifMarge) / totalVenteBrute);
      }

      return result;
    });
  }

  private Double getSummaryResult(List<Summary> summaries, List<Double> summary, String field) {
    val item = summaries
        .stream()
        .filter(sl -> sl.getSelector().equals(field))
        .findFirst();

    if (item.isEmpty())
      throw new GraphQLException("Missing mandatory field :" + field);

    return Optional.ofNullable(summary.get(summaries.indexOf(item.get()))).orElse(0d);
  };

  private Double fetchTotalMargeBrute(List<Summary> summaries, List<Double> summary) {
    Double totalVenteBrut = Optional.ofNullable(this
        .getSummaryResult(summaries, summary, "totalVenteBrut"))
        .orElse(0d);
    Double totalRemise = Optional.ofNullable(this
        .getSummaryResult(summaries, summary, "totalRemise"))
        .orElse(0d);
    Double totalRestitue = Optional.ofNullable(this
        .getSummaryResult(summaries, summary, "totalRestitue"))
        .orElse(0d);
    Double totalFraisMarketing = Optional.ofNullable(this
        .getSummaryResult(summaries, summary, "totalFraisMarketing"))
        .orElse(0d);
    Double totalAchat = Optional.ofNullable(this
        .getSummaryResult(summaries, summary, "totalAchat"))
        .orElse(0d);
    Double totalTransport = Optional.ofNullable(this
        .getSummaryResult(summaries, summary, "totalTransport"))
        .orElse(0d);
    Double totalTransit = Optional.ofNullable(this
        .getSummaryResult(summaries, summary, "totalTransit"))
        .orElse(0d);
    Double totalCourtage = Optional.ofNullable(this
        .getSummaryResult(summaries, summary, "totalCourtage"))
        .orElse(0d);
    Double totalFraisAdditionnels = Optional.ofNullable(this
        .getSummaryResult(summaries, summary, "totalFraisAdditionnels"))
        .orElse(0d);
    return totalVenteBrut - totalRemise + totalRestitue - totalFraisMarketing - totalAchat - totalTransport
        - totalTransit - totalCourtage - totalFraisAdditionnels;
  };

  /**
   * This function takes an order line and returns a new order line with all the
   * fields that are null
   * filled with default values
   * 
   * @param ordreLigne the object to be updated
   * @return the updated object.
   */
  public GeoOrdreLigne withDefaults(GeoOrdreLigne ordreLigne) {
    if (ordreLigne.getNombrePalettesExpediees() == null)
      ordreLigne.setNombrePalettesExpediees(0f);
    if (ordreLigne.getNombreColisExpedies() == null)
      ordreLigne.setNombreColisExpedies(0f);
    if (ordreLigne.getPoidsNetExpedie() == null)
      ordreLigne.setPoidsNetExpedie(0f);
    if (ordreLigne.getPoidsBrutExpedie() == null)
      ordreLigne.setPoidsBrutExpedie(0d);
    if (ordreLigne.getVenteQuantite() == null)
      ordreLigne.setVenteQuantite(0d);
    if (ordreLigne.getAchatQuantite() == null)
      ordreLigne.setAchatQuantite(0d);
    if (ordreLigne.getTotalVenteBrut() == null)
      ordreLigne.setTotalVenteBrut(0d);
    if (ordreLigne.getTotalRemise() == null)
      ordreLigne.setTotalRemise(0f);
    if (ordreLigne.getTotalRestitue() == null)
      ordreLigne.setTotalRestitue(0f);
    if (ordreLigne.getTotalFraisMarketing() == null)
      ordreLigne.setTotalFraisMarketing(0d);
    if (ordreLigne.getTotalAchat() == null)
      ordreLigne.setTotalAchat(0d);
    if (ordreLigne.getTotalObjectifMarge() == null)
      ordreLigne.setTotalObjectifMarge(0f);
    if (ordreLigne.getTotalTransport() == null)
      ordreLigne.setTotalTransport(0f);
    if (ordreLigne.getTotalTransit() == null)
      ordreLigne.setTotalTransit(0f);
    if (ordreLigne.getTotalCourtage() == null)
      ordreLigne.setTotalCourtage(0f);
    if (ordreLigne.getTotalFraisAdditionnels() == null)
      ordreLigne.setTotalFraisAdditionnels(0f);
    if (ordreLigne.getTotalFraisPlateforme() == null)
      ordreLigne.setTotalFraisPlateforme(0f);
    if (ordreLigne.getIndicateurPalette() == null)
      ordreLigne.setIndicateurPalette(0f);
    if (ordreLigne.getGratuit() == null)
      ordreLigne.setGratuit(false);
    if (ordreLigne.getExpedie() == null)
      ordreLigne.setExpedie(false);
    if (ordreLigne.getLivre() == null)
      ordreLigne.setLivre(false);
    if (ordreLigne.getBonAFacturer() == null)
      ordreLigne.setBonAFacturer(false);
    if (ordreLigne.getFacture() == null)
      ordreLigne.setFacture(false);
    if (ordreLigne.getVerificationFournisseur() == null)
      ordreLigne.setVerificationFournisseur(false);
    if (ordreLigne.getTauxRemiseSurFacture() == null)
      ordreLigne.setTauxRemiseSurFacture(0f);
    if (ordreLigne.getTauxRemiseHorsFacture() == null)
      ordreLigne.setTauxRemiseHorsFacture(0f);
    if (ordreLigne.getNombreColisManquant() == null)
      ordreLigne.setNombreColisManquant(0);
    return ordreLigne;
  }

}
