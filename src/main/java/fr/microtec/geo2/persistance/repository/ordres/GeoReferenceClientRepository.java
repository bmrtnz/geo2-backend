package fr.microtec.geo2.persistance.repository.ordres;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.persistance.entity.ordres.GeoReferenceClient;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoReferenceClientRepository extends GeoRepository<GeoReferenceClient, String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} WHERE client.id = :client and article.id IN :articles")
    void removeRefs(String client, List<String> articles);
}
