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
                result.add(getIndex.apply("pourcentageMargeNette"),
                        (totalMargeBrute - totalObjectifMarge) / totalVenteBrute);
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
     * @param chunkOrdreLigne the object to be updated
     * @return the updated object.
     */
    public GeoOrdreLigne withDefaults(GeoOrdreLigne chunkOrdreLigne) {
        if (chunkOrdreLigne.getId() != null)
            return chunkOrdreLigne;

        if (chunkOrdreLigne.getNombrePalettesExpediees() == null)
            chunkOrdreLigne.setNombrePalettesExpediees(0f);
        if (chunkOrdreLigne.getNombreColisExpedies() == null)
            chunkOrdreLigne.setNombreColisExpedies(0f);
        if (chunkOrdreLigne.getPoidsNetExpedie() == null)
            chunkOrdreLigne.setPoidsNetExpedie(0f);
        if (chunkOrdreLigne.getPoidsBrutExpedie() == null)
            chunkOrdreLigne.setPoidsBrutExpedie(0d);
        if (chunkOrdreLigne.getVenteQuantite() == null)
            chunkOrdreLigne.setVenteQuantite(0d);
        if (chunkOrdreLigne.getAchatQuantite() == null)
            chunkOrdreLigne.setAchatQuantite(0d);
        if (chunkOrdreLigne.getTotalVenteBrut() == null)
            chunkOrdreLigne.setTotalVenteBrut(0d);
        if (chunkOrdreLigne.getTotalRemise() == null)
            chunkOrdreLigne.setTotalRemise(0f);
        if (chunkOrdreLigne.getTotalRestitue() == null)
            chunkOrdreLigne.setTotalRestitue(0f);
        if (chunkOrdreLigne.getTotalFraisMarketing() == null)
            chunkOrdreLigne.setTotalFraisMarketing(0d);
        if (chunkOrdreLigne.getTotalAchat() == null)
            chunkOrdreLigne.setTotalAchat(0d);
        if (chunkOrdreLigne.getTotalObjectifMarge() == null)
            chunkOrdreLigne.setTotalObjectifMarge(0f);
        if (chunkOrdreLigne.getTotalTransport() == null)
            chunkOrdreLigne.setTotalTransport(0f);
        if (chunkOrdreLigne.getTotalTransit() == null)
            chunkOrdreLigne.setTotalTransit(0f);
        if (chunkOrdreLigne.getTotalCourtage() == null)
            chunkOrdreLigne.setTotalCourtage(0f);
        if (chunkOrdreLigne.getTotalFraisAdditionnels() == null)
            chunkOrdreLigne.setTotalFraisAdditionnels(0f);
        if (chunkOrdreLigne.getTotalFraisPlateforme() == null)
            chunkOrdreLigne.setTotalFraisPlateforme(0f);
        if (chunkOrdreLigne.getIndicateurPalette() == null)
            chunkOrdreLigne.setIndicateurPalette(0f);
        if (chunkOrdreLigne.getGratuit() == null)
            chunkOrdreLigne.setGratuit(false);
        if (chunkOrdreLigne.getExpedie() == null)
            chunkOrdreLigne.setExpedie(false);
        if (chunkOrdreLigne.getLivre() == null)
            chunkOrdreLigne.setLivre(false);
        if (chunkOrdreLigne.getBonAFacturer() == null)
            chunkOrdreLigne.setBonAFacturer(false);
        if (chunkOrdreLigne.getFacture() == null)
            chunkOrdreLigne.setFacture(false);
        if (chunkOrdreLigne.getVerificationFournisseur() == null)
            chunkOrdreLigne.setVerificationFournisseur(false);
        if (chunkOrdreLigne.getTauxRemiseSurFacture() == null)
            chunkOrdreLigne.setTauxRemiseSurFacture(0f);
        if (chunkOrdreLigne.getTauxRemiseHorsFacture() == null)
            chunkOrdreLigne.setTauxRemiseHorsFacture(0f);
        if (chunkOrdreLigne.getNombreColisManquant() == null)
            chunkOrdreLigne.setNombreColisManquant(0);
        return chunkOrdreLigne;
    }

    public void updateFromHistory(
            String newLigneRef,
            String historyLigneRef) {
        Optional<GeoOrdreLigne> target = ((GeoOrdreLigneRepository) this.repository)
                .findById(newLigneRef);
        GeoOrdreLigne original = ((GeoOrdreLigneRepository) this.repository).getOne(historyLigneRef);
        target.ifPresent(t -> {
            t.setFournisseur(original.getFournisseur());
            t.setProprietaireMarchandise(original.getProprietaireMarchandise());
            t.setVentePrixUnitaire(original.getVentePrixUnitaire());
            t.setAchatPrixUnitaire(original.getAchatPrixUnitaire());
            t.setVenteUnite(original.getVenteUnite());
            t.setAchatUnite(original.getAchatUnite());
            t.setGratuit(original.getGratuit());
            t.setCodePromo(original.getCodePromo());
            t.setAchatDevise(original.getAchatDevise());
            t.setAchatDevisePrixUnitaire(original.getAchatDevisePrixUnitaire());
            t.setTypePalette(original.getTypePalette());
            t.setPaletteInter(original.getPaletteInter());
            t.setLibelleDLV(original.getLibelleDLV());
            t.setFraisPrixUnitaire(original.getFraisPrixUnitaire());
            t.setFraisUnite(original.getFraisUnite());
            t.setFraisCommentaires(original.getFraisCommentaires());
            t.setTauxRemiseSurFacture(original.getTauxRemiseSurFacture());
            t.setTauxRemiseHorsFacture(original.getTauxRemiseHorsFacture());
            t.setArticleKit(original.getArticleKit());
            t.setGtinColisKit(original.getGtinColisKit());
            this.repository.save(t);
        });

    }

}
