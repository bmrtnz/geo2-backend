package fr.microtec.geo2.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoLigneChargement;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoLigneChargementRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.execution.ResolutionEnvironment;

@Service()
public class LigneChargementService extends GeoAbstractGraphQLService<GeoLigneChargement, String> {

    private final EntityManager entityManager;
    private final GeoOrdreRepository ordreRepository;
    private final GeoOrdreLigneRepository ordreLigneRepository;
    private final GeoFunctionOrdreRepository functionOrdreRepository;

    public LigneChargementService(GeoLigneChargementRepository ligneChargementRepository,
            GeoOrdreLigneRepository ordreLigneRepository,
            GeoOrdreRepository ordreRepository,
            EntityManager entityManager,
            GeoFunctionOrdreRepository functionOrdreRepository) {
        super(ligneChargementRepository, GeoLigneChargement.class);
        this.ordreLigneRepository = ordreLigneRepository;
        this.ordreRepository = ordreRepository;
        this.entityManager = entityManager;
        this.functionOrdreRepository = functionOrdreRepository;
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
            this.ordreLigneRepository.save(ol);
        }

        return inputs;
    }

    public GeoOrdre createOrdreChargement(String codeChargement, String originalOrdreId, String societeId) {
        GeoOrdre ordreChargement = this.ordreRepository.getOne(originalOrdreId);

        FunctionResult result = this.functionOrdreRepository.fNouvelOrdre(societeId);
        if (!result.getRes().equals(FunctionResult.RESULT_OK))
            throw new RuntimeException("Erreur de génération d'un ordre de chargement" + result.getMsg());

        ordreChargement.setCodeChargement(codeChargement);
        ordreChargement.setId(new GeoOrdre().getId());
        ordreChargement.setNumero(result.getData().get("ls_nordre").toString());

        this.entityManager.detach(ordreChargement);
        return this.ordreRepository.save(ordreChargement);
    }

}
