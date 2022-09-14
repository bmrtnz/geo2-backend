package fr.microtec.geo2.persistance.repository.ordres;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoMRUOrdreKey;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoMRUOrdreRepository extends GeoRepository<GeoMRUOrdre, GeoMRUOrdreKey> {
    @Query("SELECT mru FROM #{#entityName} mru INNER JOIN GeoOrdre o on o.id = mru.ordreRef WHERE mru.societe.id = :societe and mru.ordreRef is not null and mru.utilisateur.nomUtilisateur = :utilisateur and mru.dateModification >= :dateMax and rownum <= :count")
    List<GeoMRUOrdre> findHead(String societe, String utilisateur, LocalDateTime dateMax, Long count);
}
