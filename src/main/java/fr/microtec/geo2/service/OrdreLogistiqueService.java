package fr.microtec.geo2.service;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLogistique;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLogistiqueRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service()
public class OrdreLogistiqueService extends GeoAbstractGraphQLService<GeoOrdreLogistique, String> {

  public OrdreLogistiqueService(
      GeoOrdreLogistiqueRepository ordreLogistiqueRepository,
      EntityManager entityManager) {
    super(ordreLogistiqueRepository, GeoOrdreLogistique.class);
  }

  /**
   * It sets the default values for the fields that are null.
   * 
   * @param ordreLogistique the GeoOrdreLogistique object to be updated
   * @return The updated object.
   */
  public GeoOrdreLogistique withDefaults(GeoOrdreLogistique ordreLogistique) {
    if (ordreLogistique.getId() != null)
      ordreLogistique = OrdreLigneService.merge(this.repository.getOne(ordreLogistique.getId()), ordreLogistique, null);
    if (ordreLogistique.getExpedieStation() == null)
      ordreLogistique.setExpedieStation(false);
    if (ordreLogistique.getExpedieLieuGroupage() == null)
      ordreLogistique.setExpedieLieuGroupage(false);
    return ordreLogistique;
  }

}
