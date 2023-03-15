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

    /**
     * Structure representing an indicator count by case :
     * <ul>
     * <li>`byUser@false` + `secteur@empty` = count on all secteur (default)</li>
     * <li>`byUser@true` = count by connected user</li>
     * <li>`byUser@false` + `secteur@selected` = count on selected secteur</li>
     * </ul>
     */
    @Data
    public static class IndicateurCountResponse {
        BigDecimal count = BigDecimal.valueOf(0);
        Boolean byUser = false;
        String secteur;
    }

    public IndicateursCountService(
            GeoIndicateurCountRepository repository,
            SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public IndicateurCountResponse countClientsDepassementEncours(String societeCode, String secteurCode,
            Boolean byUser) {
        IndicateurCountResponse res = new IndicateurCountResponse();
        res.setSecteur(secteurCode);
        res.setCount(this.repository.countClientsDepassementEncours(res.getSecteur(), societeCode, byUser));
        return res;
    }

    public IndicateurCountResponse countOrdresNonConfirmes(String societeCode, String secteurCode, Boolean byUser) {
        IndicateurCountResponse res = new IndicateurCountResponse();
        res.setSecteur(secteurCode);
        res.setCount(this.repository.countOrdresNonConfirmes(res.getSecteur(), societeCode));
        return res;
    }

    public IndicateurCountResponse countPlanningDepart(String societeCode, String secteurCode, Boolean byUser) {
        IndicateurCountResponse res = new IndicateurCountResponse();
        res.setSecteur(secteurCode);
        res.setCount(this.repository.countPlanningDepart(res.getSecteur(), societeCode));
        return res;
    }

    public IndicateurCountResponse countLitigeOuvert(String societeCode, String secteurCode, Boolean byUser) {
        IndicateurCountResponse res = new IndicateurCountResponse();
        res.setSecteur(secteurCode);
        res.setCount(this.repository.countLitigeOuvert(res.getSecteur(), societeCode));
        return res;
    }

    /**
     * Try to retrieve the connected user `secteur` by `role`
     *
     * @deprecated not used anymore, choice is made in front
     */
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
