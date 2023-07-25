package fr.microtec.geo2.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.ordres.GeoCommandeEdi;
import fr.microtec.geo2.persistance.repository.ordres.GeoEdiOrdreRepository;

@Service
public class EdiOrdreService {

    private final GeoEdiOrdreRepository ediOrdreRepository;

    public EdiOrdreService(GeoEdiOrdreRepository ediOrdreRepository) {
        this.ediOrdreRepository = ediOrdreRepository;
    }

    public List<GeoCommandeEdi> allCommandeEdi(
            String secteurId, String clientId, String status, LocalDateTime dateMin,
            LocalDateTime dateMax, String assistantId, String commercialId, String ediOrdreId, String nomUtilisateur,
            String typeSearch) {
        List<GeoCommandeEdi> commandeEdiList = this.ediOrdreRepository.allCommandeEdi(secteurId, clientId, status,
                dateMin, dateMax, assistantId, commercialId, ediOrdreId, nomUtilisateur, typeSearch);

        this.fVerifStatusLigEdi(commandeEdiList);

        return commandeEdiList;
    }

    /**
     * corresponds à f_verif_status_lig_edi.pbl
     */
    private void fVerifStatusLigEdi(List<GeoCommandeEdi> commandeEdiList) {
        Map<String, List<GeoCommandeEdi>> map = new HashMap<>();

        // Groupe by REF_EDI_ORDRE
        commandeEdiList.forEach(geoCommandeEdi -> {
            if (!map.containsKey(geoCommandeEdi.getRefEdiOrdre())) {
                map.put(geoCommandeEdi.getRefEdiOrdre(), new ArrayList<>());
            }

            map.get(geoCommandeEdi.getRefEdiOrdre()).add(geoCommandeEdi);
        });

        // Set value
        map.forEach((key, geoCommandeEdis) -> {
            Boolean asValidLine = geoCommandeEdis.stream()
                    .anyMatch(GeoCommandeEdi::checkVerifStatusEdiLigne);

            geoCommandeEdis.forEach(geoCommandeEdi -> geoCommandeEdi.setVerifStatusEdi(asValidLine));
        });
    }
}
