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

    public BigDecimal countClientsDepassementEncours(String societeCode) {
        return this.repository
                .countClientsDepassementEncours(this.fetchSecteur(), societeCode);
    }

    public BigDecimal countOrdresNonConfirmes(String societeCode) {
        return this.repository
                .countOrdresNonConfirmes(this.fetchSecteur(), societeCode);
    }

    public BigDecimal countPlanningDepart(String societeCode) {
        return this.repository
                .countPlanningDepart(this.fetchSecteur(), societeCode);
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
