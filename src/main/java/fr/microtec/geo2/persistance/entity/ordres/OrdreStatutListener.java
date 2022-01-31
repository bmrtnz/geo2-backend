package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;

public class OrdreStatutListener {

    @PostLoad
    @PostUpdate
    void fetch(GeoOrdre ordre) {
        ordre.setStatut(GeoOrdreStatut.NON_CONFIRME);
		if (ordre.getFlagPublication()) ordre.setStatut(GeoOrdreStatut.CONFIRME);
		if (!ordre.getTracabiliteDetailPalettes().isEmpty()) ordre.setStatut(GeoOrdreStatut.EN_PREPARATION);
		if (!ordre.getLignes().isEmpty() && ordre.getExpedieAuComplet()) ordre.setStatut(GeoOrdreStatut.EXPEDIE);
		if (ordre.getBonAFacturer()) ordre.setStatut(GeoOrdreStatut.A_FACTURER);
		if (ordre.getFacture()) ordre.setStatut(GeoOrdreStatut.FACTURE);
		if (ordre.getFlagAnnule()) ordre.setStatut(GeoOrdreStatut.ANNULE);
    }

}
