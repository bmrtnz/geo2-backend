package fr.microtec.geo2.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.repository.ordres.GeoIndicateurCountRepository;
import fr.microtec.geo2.service.security.SecurityService;
import lombok.Data;
import lombok.val;

@Service
public class IndicateursCountService {

    private final SecurityService securityService;
    private final GeoIndicateurCountRepository repository;

    @Data
    public static class IndicateurCountResponse {
        BigDecimal count = BigDecimal.valueOf(0);
        String secteur;
    }

    public IndicateursCountService(
            GeoIndicateurCountRepository repository,
            SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public IndicateurCountResponse countClientsDepassementEncours(String societeCode, String secteurCode) {
        IndicateurCountResponse res = new IndicateurCountResponse();
        res.setSecteur(secteurCode == null ? this.fetchSecteur() : secteurCode);
        res.setCount(this.repository.countClientsDepassementEncours(res.getSecteur(), societeCode));
        return res;
    }

    public IndicateurCountResponse countOrdresNonConfirmes(String societeCode, String secteurCode) {
        IndicateurCountResponse res = new IndicateurCountResponse();
        res.setSecteur(secteurCode == null ? this.fetchSecteur() : secteurCode);
        res.setCount(this.repository.countOrdresNonConfirmes(res.getSecteur(), societeCode));
        return res;
    }

    public IndicateurCountResponse countPlanningDepart(String societeCode, String secteurCode) {
        IndicateurCountResponse res = new IndicateurCountResponse();
        res.setSecteur(secteurCode == null ? this.fetchSecteur() : secteurCode);
        res.setCount(this.repository.countPlanningDepart(res.getSecteur(), societeCode));
        return res;
    }

    private String fetchSecteur() {
        val user = this.securityService.getUser();
        val ubr = user.getUtilisateurByRole();
        if (ubr.isPresent())
            return ubr.get().getSecteurCommercial().getId();
        if (user.isAdmin())
            return "%";
        return user.getSecteurCommercial().getId();
    }

}
