package fr.microtec.geo2.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoMouvementEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoMouvementFournisseur;
import fr.microtec.geo2.persistance.entity.tiers.GeoRecapitulatifEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoRecapitulatifFournisseur;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service()
public class EntrepotService extends GeoAbstractGraphQLService<GeoEntrepot, String> {

    @PersistenceContext
    private EntityManager entityManager;

    private final GeoEntrepotRepository entrepotRepository;

    public EntrepotService(
            GeoEntrepotRepository entrepotRepository) {
        super(entrepotRepository, GeoEntrepot.class);
        this.entrepotRepository = entrepotRepository;
    }

    public List<GeoMouvementFournisseur> allMouvementFournisseur(
            LocalDateTime dateMaxMouvements,
            String codeSociete,
            String codeEntrepot,
            String codeCommercial,
            String codeFournisseur) {
        return this.entrepotRepository
                .allMouvementFournisseur(
                        dateMaxMouvements,
                        codeSociete,
                        codeEntrepot,
                        codeCommercial,
                        codeFournisseur);
    }

    public List<GeoMouvementEntrepot> allMouvementEntrepot(
            LocalDateTime dateMaxMouvements,
            String codeSociete,
            String codeEntrepot,
            String codeCommercial,
            String codeFournisseur) {
        return this.entrepotRepository
                .allMouvementEntrepot(
                        dateMaxMouvements,
                        codeSociete,
                        codeEntrepot,
                        codeCommercial,
                        codeFournisseur);
    }

    public List<GeoRecapitulatifFournisseur> allRecapitulatifFournisseur(
            LocalDateTime dateMaxMouvements,
            String codeSociete,
            String codeEntrepot,
            String codeFournisseur) {
        return this.entrepotRepository
                .allRecapitulatifFournisseur(
                        dateMaxMouvements,
                        codeSociete,
                        codeEntrepot,
                        codeFournisseur);
    }

    public List<GeoRecapitulatifEntrepot> allRecapitulatifEntrepot(
            LocalDateTime dateMaxMouvements,
            String codeSociete,
            String codeEntrepot,
            String codeCommercial,
            String codeFournisseur) {
        return this.entrepotRepository
                .allRecapitulatifEntrepot(
                        dateMaxMouvements,
                        codeSociete,
                        codeEntrepot,
                        codeCommercial,
                        codeFournisseur);
    }
}
