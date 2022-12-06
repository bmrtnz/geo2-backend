package fr.microtec.geo2.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.ordres.GeoLigneChargement;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.repository.ordres.GeoLigneChargementRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;
import io.leangen.graphql.execution.ResolutionEnvironment;

@Service()
public class LigneChargementService extends GeoAbstractGraphQLService<GeoLigneChargement, String> {

    private final GeoOrdreLigneRepository ordreLigneRepository;

    public LigneChargementService(GeoLigneChargementRepository ligneChargementRepository,
            GeoOrdreLigneRepository ordreLigneRepository) {
        super(ligneChargementRepository, GeoLigneChargement.class);
        this.ordreLigneRepository = ordreLigneRepository;
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

}
