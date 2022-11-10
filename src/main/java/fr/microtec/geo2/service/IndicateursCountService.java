package fr.microtec.geo2.service;

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

    public long countClientsDepassementEncours(String societeCode) {
        return this.repository
                .countClientsDepassementEncours(this.fetchSecteur(), societeCode);
    }

    public long countOrdresNonConfirmes(String societeCode) {
        return this.repository
                .countOrdresNonConfirmes(this.fetchSecteur(), societeCode);
    }

    public long countPlanningDepart(String societeCode) {
        return this.repository
                .countPlanningDepart(this.fetchSecteur(), societeCode);
    }

    private String fetchSecteur() {
        val user = this.securityService.getUser();
        return user.isAdmin() ? "%" : user.getUtilisateurByRole().getSecteurCommercial().getId();
    }

}
