package fr.microtec.geo2.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.stock.GeoOrdre;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.repository.stock.GeoOrdreRepository;

@Service()
public class OrdreService {

  @PersistenceContext
  private EntityManager entityManager;

  private final GeoOrdreRepository ordreRepository;

  public OrdreService(GeoOrdreRepository ordreRepository) {
    this.ordreRepository = ordreRepository;
  }

  private String fetchNumero(GeoSociete societe) {

    String societeId = societe.getId();
    String sequenceQuery = String.format("SELECT TO_CHAR(seq_nordre_%s.NEXTVAL,'FM099999') FROM DUAL", societeId);
    Session session = this.entityManager.unwrap(Session.class);
    SessionFactory factory = session.getSessionFactory();
    NativeQuery query = factory.openSession().createNativeQuery(sequenceQuery);

    return query.getSingleResult().toString();
  }

  public GeoOrdre save(GeoOrdre ordreChunk) {
    if (ordreChunk.getId() == null)
      ordreChunk.setNumero(this.fetchNumero(ordreChunk.getSociete()));
    return this.ordreRepository.save(ordreChunk);
  }

}
