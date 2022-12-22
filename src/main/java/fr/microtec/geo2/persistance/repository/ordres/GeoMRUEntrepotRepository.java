package fr.microtec.geo2.persistance.repository.ordres;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUEntrepot;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUEntrepotKey;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoMRUEntrepotRepository extends GeoRepository<GeoMRUEntrepot, GeoMRUEntrepotKey> {
    @Transactional
    @Modifying
    void deleteOneByEntrepotIdAndUtilisateur(String entrepotId, GeoUtilisateur utilisateur);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM #{#entityName} WHERE utilisateur = :utilisateur")
    void deleteAllByUtilisateur(GeoUtilisateur utilisateur);
}
