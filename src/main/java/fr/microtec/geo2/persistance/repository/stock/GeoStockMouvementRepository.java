package fr.microtec.geo2.persistance.repository.stock;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.stock.GeoStockMouvement;
import fr.microtec.geo2.persistance.repository.GeoRepository;

@Repository
public interface GeoStockMouvementRepository extends GeoRepository<GeoStockMouvement, String> {
    @Transactional
    @Modifying
    @Query("delete from GeoStockMouvement where ordreLigne.id = ?1")
    void deleteAllByOrdreLigneId(String ordreLigneId);
}
