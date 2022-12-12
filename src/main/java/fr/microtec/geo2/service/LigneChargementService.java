package fr.microtec.geo2.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.common.GeoCampagne;
import fr.microtec.geo2.persistance.entity.ordres.GeoLigneChargement;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoLigneChargementRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoEnvoisRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoFluxRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoSocieteRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.execution.ResolutionEnvironment;

@Service()
public class LigneChargementService extends GeoAbstractGraphQLService<GeoLigneChargement, String> {

    private final EntityManager entityManager;
    private final OrdreLigneService ordreLigneService;

    private final GeoOrdreRepository ordreRepository;
    private final GeoOrdreLigneRepository ordreLigneRepository;
    private final GeoFunctionOrdreRepository functionOrdreRepository;
    private final GeoEnvoisRepository envoisRepository;
    private final GeoFluxRepository fluxRepository;
    private final GeoSocieteRepository societeRepository;

    public LigneChargementService(GeoLigneChargementRepository ligneChargementRepository,
            OrdreLigneService ordreLigneService,
            GeoOrdreLigneRepository ordreLigneRepository,
            GeoOrdreRepository ordreRepository,
            EntityManager entityManager,
            GeoFunctionOrdreRepository functionOrdreRepository,
            GeoEnvoisRepository envoisRepository,
            GeoFluxRepository fluxRepository,
            GeoSocieteRepository societeRepository) {
        super(ligneChargementRepository, GeoLigneChargement.class);
        this.ordreLigneService = ordreLigneService;
        this.ordreLigneRepository = ordreLigneRepository;
        this.ordreRepository = ordreRepository;
        this.entityManager = entityManager;
        this.functionOrdreRepository = functionOrdreRepository;
        this.envoisRepository = envoisRepository;
        this.fluxRepository = fluxRepository;
        this.societeRepository = societeRepository;
    }

    public List<GeoLigneChargement> saveAll(List<GeoLigneChargement> inputs,
            ResolutionEnvironment env) {

        // save data
        for (GeoLigneChargement ligne : inputs) {
            GeoOrdreLigne ol = this.ordreLigneRepository.getOne(ligne.getId());
            if (ligne.getDateDepartPrevue() != null)
                ol.getOrdre().setDateDepartPrevue(ligne.getDateDepartPrevue());
            if (ligne.getDateLivraisonPrevue() != null)
                ol.getOrdre().setDateLivraisonPrevue(ligne.getDateLivraisonPrevue());
            if (ligne.getDateDepartPrevueFournisseur() != null)
                ol.getLogistique().setDateDepartPrevueFournisseur(ligne.getDateDepartPrevueFournisseur());
            if (ligne.getNumeroCamion() != null)
                ol.getOrdre().setNumeroCamion(ligne.getNumeroCamion());
            if (ligne.getOrdreChargement() != null)
                ol.getOrdre().setOrdreChargement(ligne.getOrdreChargement());
            this.ordreLigneRepository.save(Hibernate.unproxy(ol, GeoOrdreLigne.class));
            ligne.setLigne(ol);
        }

        return inputs;
    }

    private GeoOrdre createOrdreChargement(String codeChargement, String originalOrdreId, String societeId) {

        FunctionResult result = this.functionOrdreRepository.fNouvelOrdre(societeId);
        if (!result.getRes().equals(FunctionResult.RESULT_OK))
            throw new RuntimeException("Erreur de génération d'un ordre de chargement" + result.getMsg());

        String numero = result.getData().get("ls_nordre").toString();
        this.ordreRepository.createChargement(numero, codeChargement, originalOrdreId);

        GeoCampagne campagne = this.ordreRepository.getOne(originalOrdreId).getCampagne();
        GeoSociete societe = this.societeRepository.getOne(societeId);
        return this.ordreRepository.findByNumeroAndSocieteAndCampagne(numero, societe, campagne).get();

    }

    /** Apply control on each ordre-lignes, throw exception on failure */
    private void controlLignes(List<String> ol) {
        List<GeoOrdreLigne> fetched = this.ordreLigneRepository
                .findAll((root, cq, cb) -> root.get("id").in(ol));

        // Verifying for common entrepots
        String entrepotReferent = fetched.get(0).getOrdre().getEntrepot().getId();
        if (fetched.parallelStream()
                .anyMatch(ligne -> !ligne.getOrdre().getEntrepot().getId().equals(entrepotReferent)))
            throw new RuntimeException("Les entrepôts sont différents !");

        // Check for nonexisting "command confirmation"
        if (fetched.parallelStream().anyMatch(
                ligne -> this.envoisRepository.countByOrdreAndFlux(ligne.getOrdre(),
                        this.fluxRepository.getOne("ORDRE")) > 0))
            throw new RuntimeException("Des \"confirmations de commande\" sont déjà présents pour ces ordres !");

    }

    /** Transfer ordre-lignes input to a chargement */
    public List<GeoOrdreLigne> transfer(
            List<String> inputs,
            String codeChargement,
            String originalOrdreId,
            String societeId) {
        this.controlLignes(inputs);
        GeoOrdre target = this.createOrdreChargement(codeChargement, originalOrdreId, societeId);

        List<GeoOrdreLigne> fetched = this.ordreLigneRepository
                .findAll((root, cq, cb) -> root.get("id").in(inputs))
                .parallelStream()
                .map(ligne -> {
                    ligne.setOrdre(target);
                    return ligne;
                })
                .collect(Collectors.toList());

        return this.ordreLigneRepository.saveAll(fetched);
    }

    /** Duplicate ordre-lignes input to a chargement */
    @Transactional
    public List<GeoOrdreLigne> duplicate(
            List<String> inputs,
            String codeChargement,
            String originalOrdreId,
            String societeId) {
        this.controlLignes(inputs);
        GeoOrdre target = this.createOrdreChargement(codeChargement, originalOrdreId, societeId);

        return this.ordreLigneRepository
                .findAll((root, cq, cb) -> root.get("id").in(inputs))
                .stream()
                .map(ligne -> {
                    String id = this.ordreLigneService.generateId();
                    this.ordreLigneRepository.duplicateForChargement(id, target.getId(), ligne.getId());
                    return this.ordreLigneRepository.getOne(id);
                })
                .collect(Collectors.toList());
    }

}
