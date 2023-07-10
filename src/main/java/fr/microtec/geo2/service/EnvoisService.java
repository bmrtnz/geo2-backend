package fr.microtec.geo2.service;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.GeoSequenceGenerator;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.tiers.GeoEnvois;
import fr.microtec.geo2.persistance.entity.tiers.GeoFlux;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoEnvoisRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoFluxRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import fr.microtec.geo2.service.security.SecurityService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnvoisService extends GeoAbstractGraphQLService<GeoEnvois, String> {

    private final EntityManager entityManager;
    private final GeoOrdreRepository ordreRepository;
    private final GeoFluxRepository fluxRepository;
    private final SecurityService securityService;

    public EnvoisService(
            GeoEnvoisRepository paysRepository,
            EntityManager entityManager,
            SecurityService securityService,
            GeoOrdreRepository ordreRepository,
            GeoFluxRepository fluxRepository) {
        super(paysRepository, GeoEnvois.class);
        this.entityManager = entityManager;
        this.securityService = securityService;
        this.ordreRepository = ordreRepository;
        this.fluxRepository = fluxRepository;
    }

    /**
     * > It takes a list of GeoEnvois, finds the original GeoEnvois in the database,
     * merges the two, and
     * returns a list of the merged GeoEnvois.
     * Provided `id` is mandatory and used to fetch the original entity.
     *
     * @param envois The list of GeoEnvois chunks to be merged.
     * @return A list of GeoEnvois
     */
    public List<GeoEnvois> duplicateMergeAll(List<GeoEnvois> envois) {
        String numeroDemande = this.fetchEnvoiDemandeNumero();
        return envois.stream()
                .map(envoi -> {
                    GeoEnvois original = this.repository.findById(envoi.getId()).orElseThrow();
                    envoi = GeoAbstractGraphQLService.merge(envoi, original, null);
                    this.entityManager.detach(envoi);
                    this.entityManager.detach(original);
                    envoi.setId(null);
                    envoi.setNumeroDemande(numeroDemande);
                    envoi.setUserModification(this.securityService.getUser().getNomUtilisateur());
                    return envoi;
                })
                .collect(Collectors.toList());
    }

    private String fetchEnvoiDemandeNumero() {
        Properties params = new Properties();

        params.put(GeoSequenceGenerator.SEQUENCE_PARAM, "seq_end_num");
        params.put(GeoSequenceGenerator.IS_SEQUENCE_PARAM, true);
        params.put(GeoSequenceGenerator.MASK_PARAM, "FM0XXXX");
        params.put(GeoSequenceGenerator.PREPEND_PARAM, "E");

        return (String) GeoSequenceGenerator.generate(this.entityManager, params);
    }

    /**
     * "Delete all GeoEnvois entities where the ordre is the given ordre and the
     * traite is either 'A' or 'R'."
     */
    public void clearTemp(GeoOrdre ordre) {
        Specification<GeoEnvois> spec = Specification.where(null);
        spec = (root, query, cb) -> {
            Predicate whereOrdre = cb.equal(root.get("ordre"), ordre);
            Predicate whereTraite = root.get("traite").in(List.of('A', 'R'));
            // Predicate merged = cb.and(whereOrdre, whereTraite);
            // query.select(root.get("id")).where(merged);
            return cb.and(whereOrdre, whereTraite);
        };
        List<GeoEnvois> entities = this.repository.findAll(spec);
        try {
            this.repository.deleteAll(entities);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Count `envois` from specified `ordre` where `flux` is `INCLIT` or `RESLIT`
     */
    public Long countLitigeEnvois(String ordreID) {
        GeoOrdre ordre = this.ordreRepository.getOne(ordreID);
        GeoFlux fluxInclit = this.fluxRepository.getOne("INCLIT");
        GeoFlux fluxReslit = this.fluxRepository.getOne("RESLIT");
        List<Character> traitement = List.of('N', 'O');
        Long countInclit = ((GeoEnvoisRepository) this.repository)
                .countByOrdreAndFluxAndTraiteIn(ordre, fluxInclit, traitement);
        Long countReslit = ((GeoEnvoisRepository) this.repository)
                .countByOrdreAndFluxAndTraiteIn(ordre, fluxReslit, traitement);
        return countInclit + countReslit;
    }

}
