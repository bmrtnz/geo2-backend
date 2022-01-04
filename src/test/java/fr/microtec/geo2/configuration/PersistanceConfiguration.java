package fr.microtec.geo2.configuration;

import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistanceConfiguration {

    @Bean
    public GeoFunctionOrdreRepository functionOrdreRepository() {
        return new GeoFunctionOrdreRepositoryImpl();
    }

}
