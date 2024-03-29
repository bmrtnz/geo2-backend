package fr.microtec.geo2.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import fr.microtec.geo2.common.CustomUtils;
import fr.microtec.geo2.configuration.graphql.PageFactory;
import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.persistance.CriteriaUtils;
import fr.microtec.geo2.persistance.GeoSequenceGenerator;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.entity.common.GeoTypeVente;
import fr.microtec.geo2.persistance.entity.litige.GeoLitige;
import fr.microtec.geo2.persistance.entity.litige.GeoLitigeLigneTotaux;
import fr.microtec.geo2.persistance.entity.ordres.GeoFactureAvoir;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreBaf;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreRegroupement;
import fr.microtec.geo2.persistance.entity.ordres.GeoPlanningTransporteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoDevise;
import fr.microtec.geo2.persistance.entity.tiers.GeoEnvois;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.common.GeoCampagneRepository;
import fr.microtec.geo2.persistance.repository.litige.GeoLitigeLigneRepository;
import fr.microtec.geo2.persistance.repository.litige.GeoLitigeRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoSocieteRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.graphql.ordres.GeoOrdreGraphQLService;
import io.leangen.graphql.execution.ResolutionEnvironment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service()
public class OrdreService extends GeoAbstractGraphQLService<GeoOrdre, String> {

    @PersistenceContext
    private EntityManager entityManager;

    private final GeoOrdreRepository ordreRepository;
    private final GeoLitigeRepository litigeRepository;
    private final GeoLitigeLigneRepository litigeLigneRepository;
    private final GeoSocieteRepository societeRepository;
    private final GeoCampagneRepository campagneRepository;
    private final GeoFunctionOrdreRepository functionOrdreRepository;

    public OrdreService(
            GeoOrdreRepository ordreRepository,
            GeoLitigeRepository litigeRepository,
            GeoLitigeLigneRepository litigeLigneRepository,
            GeoSocieteRepository societeRepository,
            GeoCampagneRepository campagneRepository,
            GeoFunctionOrdreRepository functionOrdreRepository) {
        super(ordreRepository, GeoOrdre.class);
        this.ordreRepository = ordreRepository;
        this.litigeRepository = litigeRepository;
        this.litigeLigneRepository = litigeLigneRepository;
        this.societeRepository = societeRepository;
        this.campagneRepository = campagneRepository;
        this.functionOrdreRepository = functionOrdreRepository;
    }

    private String fetchNumero(GeoSociete societe) {
        Properties params = new Properties();

        params.put("sequenceName", String.format("seq_nordre_%s", societe.getId()));
        params.put("isSequence", true);
        params.put("mask", "FM099999");

        return (String) GeoSequenceGenerator.generate(this.entityManager, params);
    }

    public GeoOrdre save(GeoOrdre ordreChunk, ResolutionEnvironment env) {
        String entityArgumentKey = CustomUtils.classToArgument(GeoOrdre.class);
        Map<String, Object> parsedArguments = CustomUtils.parseArgumentFromEnv(env, entityArgumentKey);
        if (ordreChunk.getId() == null) {
            if (ordreChunk.getNumero() == null)
                ordreChunk.setNumero(this.fetchNumero(ordreChunk.getSociete()));
            ordreChunk.setFlagGeo2(true);
            return this.ordreRepository.save(this.withDefaults(ordreChunk));
        } else {
            Optional<GeoOrdre> ordre = this.ordreRepository.findById(ordreChunk.getId());
            GeoOrdre merged = GeoOrdreGraphQLService.merge(ordreChunk, ordre.get(), parsedArguments);
            return this.ordreRepository.save(merged);
        }
    }

    public List<GeoOrdre> save(List<GeoOrdre> ordresChunk) {
        Stream<GeoOrdre> mappedOrdres = ordresChunk.stream()
                .map(chunk -> {
                    if (chunk.getId() == null) {
                        chunk.setNumero(this.fetchNumero(chunk.getSociete()));
                        return chunk;
                    }
                    Optional<GeoOrdre> ordre = this.ordreRepository.findById(chunk.getId());
                    return GeoOrdreGraphQLService.merge(chunk, ordre.get(), null);
                });

        return this.ordreRepository.saveAll(mappedOrdres.collect(Collectors.toList()));

    }

    public GeoOrdre clone(GeoOrdre chunk, ResolutionEnvironment env) {
        GeoOrdre original = this.ordreRepository.getOne(chunk.getId());
        GeoOrdre clone = original.duplicate();
        return this.save(clone, env);
    }

    public Float fetchSommeColisCommandes(GeoOrdre ordre) {
        return this
                .fetchSum(ordre, "logistiques.lignes.nombreColisCommandes")
                .floatValue();
    }

    public Float fetchSommeColisExpedies(GeoOrdre ordre) {
        return this
                .fetchSum(ordre, "logistiques.lignes.nombreColisExpedies")
                .floatValue();
    }

    public Number fetchSum(GeoOrdre ordre, String path) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Number> criteriaQuery = criteriaBuilder.createQuery(Number.class);
        Root<GeoOrdre> root = criteriaQuery.from(GeoOrdre.class);

        criteriaQuery.select(criteriaBuilder.sum(CriteriaUtils.toExpressionRecursively(root, path, false)));
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), ordre.getId()));

        TypedQuery<Number> q = this.entityManager.createQuery(criteriaQuery);
        final Number singleResult = q.getSingleResult();
        return (singleResult == null) ? 0 : singleResult;
    }

    public RelayPage<GeoOrdre> fetchOrdresPlanningTransporteurs(String search, Pageable pageable,
            final Set<String> fields) {

        Specification<GeoOrdre> spec = (Specification<GeoOrdre>) CriteriaUtils.groupedBySelection(fields);

        if (search != null && !search.isBlank())
            spec = spec.and(this.parseSearch(search));

        Page<GeoOrdre> page = this.repository
                .findAllWithPagination(spec, pageable, GeoOrdre.class, fields);

        return PageFactory.asRelayPage(page);
    }

    public RelayPage<GeoOrdre> allOrdreNonConfirmes(
            String search,
            Pageable pageable,
            Set<String> fields) {

        Specification<GeoOrdre> innerSpec = (ro, cq, cb) -> {
            Subquery<GeoEnvois> subQuery = cq.subquery(GeoEnvois.class);
            Root<GeoEnvois> re = subQuery.from(GeoEnvois.class);

            subQuery
                    .select(re.get("id"))
                    .where(
                            cb.equal(ro.get("id"), re.get("ordre").get("id")),
                            re.get("traite").in('N', 'O'),
                            cb.equal(re.get("flux").get("id"), "ORDRE"));

            cq.where(cb.equal(ro.get("factureAvoir"), GeoFactureAvoir.FACTURE));
            return cb.not(cb.exists(subQuery));
        };

        Page<GeoOrdre> page = this.repository
                .findAllWithPagination(innerSpec.and(this.parseSearch(search)), pageable, GeoOrdre.class, fields);

        return PageFactory.asRelayPage(page);

    }

    public Optional<GeoLitigeLigneTotaux> fetchLitigeLignesTotaux(String litigeID) {
        GeoLitige litige = this.litigeRepository.getOne(litigeID);
        return this.litigeLigneRepository.getTotaux(litige);
    }

    public Optional<GeoOrdre> getOneByNumeroAndSocieteAndCampagne(String numero, String societeID, String campagneID) {
        GeoSociete societe = this.societeRepository.getOne(societeID);
        GeoCampagne campagne = this.campagneRepository.getOne(campagneID);
        return this.ordreRepository.findByNumeroAndSocieteAndCampagne(numero, societe, campagne);
    }

    /**
     * Return the number of order not closed
     *
     * @param search the search string
     * @return The number of order not closed
     */
    public long fetchNombreOrdreNonCloture(final String search) {

        Specification<GeoOrdre> spec = null;

        if (StringUtils.hasText(search)) {
            spec = Specification.where(this.parseSearch(search));
        }

        return this.repository.count(spec);
    }

    public List<GeoPlanningTransporteur> allPlanningTransporteurs(
            LocalDateTime dateMin,
            LocalDateTime dateMax,
            String societeCode,
            String transporteurCode,
            String bureauAchatCode) {
        List<GeoPlanningTransporteur> list = this.ordreRepository
                .allPlanningTransporteurs(
                        dateMin,
                        dateMax,
                        societeCode,
                        transporteurCode,
                        bureauAchatCode);

        return list;
    }

    @Transactional
    public List<GeoOrdreBaf> allDepartBaf(
            String societeCode,
            String secteurCode,
            String clientCode,
            String entrepotCode,
            LocalDate dateMin,
            LocalDate dateMax,
            String codeAssistante,
            String codeCommercial) {
        Assert.hasText(societeCode, "Code société obligatoire");
        Assert.hasText(secteurCode, "Code secteur obligatoire");

        FunctionResult result = this.functionOrdreRepository.fAfficheOrdreBaf(societeCode, secteurCode, clientCode,
                entrepotCode, dateMin, dateMax, codeAssistante, codeCommercial);

        return result.getCursorDataAs(GeoOrdreBaf.class);
    }

    /**
     * It sets default values for the fields of the GeoOrdre object.
     *
     * @param ordre the GeoOrdre object to be updated
     * @return The updated object.
     */
    public GeoOrdre withDefaults(GeoOrdre ordre) {
        if (ordre.getVenteACommission() == null)
            ordre.setVenteACommission(false);
        if (ordre.getExpedie() == null)
            ordre.setExpedie(false);
        if (ordre.getLivre() == null)
            ordre.setLivre(false);
        if (ordre.getBonAFacturer() == null)
            ordre.setBonAFacturer(false);
        if (ordre.getFacture() == null)
            ordre.setFacture(false);
        if (ordre.getBonAGenererDansQualifelPlus() == null)
            ordre.setBonAGenererDansQualifelPlus(false);
        if (ordre.getGenereDansQualifelPlus() == null)
            ordre.setGenereDansQualifelPlus(false);
        if (ordre.getBonAGenererUDC() == null)
            ordre.setBonAGenererUDC(false);
        if (ordre.getGenereUDC() == null)
            ordre.setGenereUDC(false);
        if (ordre.getFactureEDIFACT() == null)
            ordre.setFactureEDIFACT(false);
        if (ordre.getPrixUnitaireTarifTransport() == null)
            ordre.setPrixUnitaireTarifTransport(0f);
        if (ordre.getPrixUnitaireTarifCourtage() == null)
            ordre.setPrixUnitaireTarifCourtage(0f);
        if (ordre.getTauxRemiseFacture() == null)
            ordre.setTauxRemiseFacture(0f);
        if (ordre.getTauxRemiseHorsFacture() == null)
            ordre.setTauxRemiseHorsFacture(0f);
        if (ordre.getTauxDevise() == null)
            ordre.setTauxDevise(0d);
        if (ordre.getTotalVente() == null)
            ordre.setTotalVente(0f);
        if (ordre.getTotalRemise() == null)
            ordre.setTotalRemise(0f);
        if (ordre.getTotalRestitue() == null)
            ordre.setTotalRestitue(0f);
        if (ordre.getTotalFraisMarketing() == null)
            ordre.setTotalFraisMarketing(0d);
        if (ordre.getTotalAchat() == null)
            ordre.setTotalAchat(0d);
        if (ordre.getTotalObjectifMarge() == null)
            ordre.setTotalObjectifMarge(0f);
        if (ordre.getTotalTransport() == null)
            ordre.setTotalTransport(0f);
        if (ordre.getTotalTransit() == null)
            ordre.setTotalTransit(0f);
        if (ordre.getTotalCourtage() == null)
            ordre.setTotalCourtage(0f);
        if (ordre.getTotalFraisPlateforme() == null)
            ordre.setTotalFraisPlateforme(0f);
        if (ordre.getTransporteurDEVPrixUnitaire() == null)
            ordre.setTransporteurDEVPrixUnitaire(0d);
        if (ordre.getTypeVente() == null)
            ordre.setTypeVente(GeoTypeVente.getDefault());
        if (ordre.getPrixTransportVisible() == null)
            ordre.setPrixTransportVisible(false);
        if (ordre.getPrixTransitVisible() == null)
            ordre.setPrixTransitVisible(false);
        if (ordre.getPrixCourtageVisible() == null)
            ordre.setPrixCourtageVisible(false);
        if (ordre.getFlagPublication() == null)
            ordre.setFlagPublication(false);
        if (ordre.getFlagAnnule() == null)
            ordre.setFlagAnnule(false);
        if (ordre.getTransporteurDEVCode() == null)
            ordre.setTransporteurDEVCode(GeoDevise.getDefault());
        if (ordre.getTransporteurDEVTaux() == null)
            ordre.setTransporteurDEVTaux(1f);
        if (ordre.getExclusionFraisPU() == null)
            ordre.setExclusionFraisPU(false);
        return ordre;
    }

    public String fetchDescriptifRegroupement(String ordreID) {
        GeoOrdre ordre = this.getOne(ordreID).orElseThrow();
        String value = "";
        String ls_nordre_sa = "";
        String ls_nordre_sa_n = "";
        String ls_nordre_buk = "";

        if (ordre.getType().getId().equals("RGP"))
            value += "Ordres regroupés : " + ordre.getListeNumeroOrigine();
        if (ordre.getType().getId().equals("ORI"))
            value += "Ordre de regroupement : " + ordre.getNumeroRGP();

        // Cas clients specifiques
        try {
            if (ordre.getClient().getId().equals("007396")) {
                Object[] res;
                if (ordre.getFactureAvoir().equals(GeoFactureAvoir.FACTURE))
                    res = (Object[]) this.entityManager
                            .createNativeQuery(
                                    "select SA.NORDRE, SA_N.NORDRE as n FROM GEO_ORDRE_BUK_SA OBS,  GEO_ORDRE SA, GEO_ORDRE SA_N where OBS.ORD_rEF_BUK =:arg_ord_ref and OBS.ORD_REF_SA= SA.ORD_REF  and OBS.ORD_REF_SA_N= SA_N.ORD_REF")
                            .setParameter("arg_ord_ref", ordreID)
                            .getSingleResult();
                else
                    res = (Object[]) this.entityManager
                            .createNativeQuery(
                                    "select 	SA.NORDRE, SA_N.NORDRE as n FROM GEO_AVOIR_BUK_SA OBS,  GEO_ORDRE SA, GEO_ORDRE SA_N where OBS.ORD_rEF_BUK =:arg_ord_ref and OBS.ORD_REF_SA= SA.ORD_REF  and 	OBS.ORD_REF_SA_N= SA_N.ORD_REF")
                            .setParameter("arg_ord_ref", ordreID)
                            .getSingleResult();
                ls_nordre_sa = (String) res[0];
                ls_nordre_sa_n = (String) res[1];
            }
        } catch (Exception exception) {
            log.info("Pas d'infos de regroupement pour le client 007396", exception);
        }

        try {
            if (ordre.getClient().getId().equals("002676")) {
                Object[] res;
                if (ordre.getFactureAvoir().equals(GeoFactureAvoir.FACTURE))
                    res = (Object[]) this.entityManager
                            .createNativeQuery(
                                    "select 	BUK.NORDRE, SA_N.NORDRE as n FROM GEO_ORDRE_BUK_SA OBS,  GEO_ORDRE BUK, GEO_ORDRE SA_N where OBS.ORD_REF_SA =:arg_ord_ref and OBS.ORD_REF_BUK= BUK.ORD_REF  and 	OBS.ORD_REF_SA_N= SA_N.ORD_REF ")
                            .setParameter("arg_ord_ref", ordreID)
                            .getSingleResult();
                else
                    res = (Object[]) this.entityManager
                            .createNativeQuery(
                                    "select 	 BUK.NORDRE, SA_N.NORDRE as n FROM GEO_AVOIR_BUK_SA OBS,  GEO_ORDRE BUK, GEO_ORDRE SA_N where OBS.ORD_rEF_SA =:arg_ord_ref and OBS.ORD_REF_BUK= BUK.ORD_REF  and 	OBS.ORD_REF_SA_N= SA_N.ORD_REF")
                            .setParameter("arg_ord_ref", ordreID)
                            .getSingleResult();
                ls_nordre_buk = (String) res[0];
                ls_nordre_sa_n = (String) res[1];
            }
        } catch (Exception exception) {
            log.info("Pas d'infos de regroupement pour le client 002676", exception);
        }

        try {
            if (ordre.getClient().getId().equals("007657")) {
                Object[] res;
                if (ordre.getFactureAvoir().equals(GeoFactureAvoir.AVOIR))
                    res = (Object[]) this.entityManager
                            .createNativeQuery(
                                    "select 	BUK.NORDRE, SA.NORDRE as n FROM GEO_ORDRE_BUK_SA OBS,  GEO_ORDRE BUK, GEO_ORDRE SA where OBS.ORD_REF_SA_N =:arg_ord_ref and OBS.ORD_REF_BUK= BUK.ORD_REF  and 	OBS.ORD_REF_SA= SA.ORD_REF")
                            .setParameter("arg_ord_ref", ordreID)
                            .getSingleResult();
                else
                    res = (Object[]) this.entityManager
                            .createNativeQuery(
                                    "select 	 BUK.NORDRE, SA.NORDRE as n FROM GEO_AVOIR_BUK_SA OBS,  GEO_ORDRE BUK, GEO_ORDRE SA where OBS.ORD_rEF_SA_N =:arg_ord_ref and OBS.ORD_REF_BUK= BUK.ORD_REF  and 	OBS.ORD_REF_SA= SA.ORD_REF ")
                            .setParameter("arg_ord_ref", ordreID)
                            .getSingleResult();
                ls_nordre_buk = (String) res[0];
                ls_nordre_sa = (String) res[1];
            }
        } catch (Exception exception) {
            log.info("Pas d'infos de regroupement pour le client 007657", exception);
        }

        if (!ls_nordre_sa.isEmpty()) {
            if (!value.isBlank())
                value += " /";
            value += " TESCO SA : " + ls_nordre_sa;
        }
        if (!ls_nordre_buk.isEmpty()) {
            if (!value.isBlank())
                value += " /";
            value += " TESCO BUK : " + ls_nordre_buk;
        }
        if (!ls_nordre_sa_n.isEmpty()) {
            if (!value.isBlank())
                value += " /";
            value += " TESCO2 SA : " + ls_nordre_sa_n;
        }

        return value;
    }

    public Boolean fetchABloquer(GeoOrdre ordre) {
        return !(this.ordreRepository.aBloquer(ordre.getId()) == null);
    }

    public List<GeoOrdreRegroupement> allOrdresRegroupement(
            LocalDateTime dateMin,
            LocalDateTime dateMax,
            String transporteurCode,
            String stationCode,
            String commercialCode) {
        return this.ordreRepository
                .allOrdresRegroupement(
                        dateMin,
                        dateMax,
                        transporteurCode,
                        stationCode,
                        commercialCode);
    }
}
