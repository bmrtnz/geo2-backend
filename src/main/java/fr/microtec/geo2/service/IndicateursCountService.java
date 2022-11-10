package fr.microtec.geo2.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.repository.ordres.GeoIndicateurCountRepository;
import fr.microtec.geo2.service.security.SecurityService;

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

    public BigDecimal countClientsDepassementEncours() {
        return this.repository.countClientsDepassementEncours();
    }

}
