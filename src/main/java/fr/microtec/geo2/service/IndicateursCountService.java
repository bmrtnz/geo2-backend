package fr.microtec.geo2.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.repository.ordres.GeoIndicateurCountRepository;
import fr.microtec.geo2.service.security.SecurityService;
import lombok.val;

@Service
public class IndicateursCountService {

    private final SecurityService securityService;
    private final GeoIndicateurCountRepository repository;

    public IndicateursCountService(
            GeoIndicateurCountRepository repository,
            SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;
    }

    public BigDecimal countClientsDepassementEncours(String societeCode, String secteurCode) {
        return this.repository
                .countClientsDepassementEncours(secteurCode == null ? this.fetchSecteur() : secteurCode, societeCode);
    }

    public BigDecimal countOrdresNonConfirmes(String societeCode, String secteurCode) {
        return this.repository
                .countOrdresNonConfirmes(secteurCode == null ? this.fetchSecteur() : secteurCode, societeCode);
    }

    public BigDecimal countPlanningDepart(String societeCode, String secteurCode) {
        return this.repository
                .countPlanningDepart(secteurCode == null ? this.fetchSecteur() : secteurCode, societeCode);
    }

    private String fetchSecteur() {
        val user = this.securityService.getUser();
        val ubr = Optional.ofNullable(user.getUtilisateurByRole());
        if (ubr.isPresent())
            return ubr.get().getSecteurCommercial().getId();
        if (user.isAdmin())
            return "%";
        return user.getSecteurCommercial().getId();
    }

}
