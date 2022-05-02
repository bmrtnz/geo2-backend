package fr.microtec.geo2.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.tiers.GeoEnvois;
import fr.microtec.geo2.persistance.repository.tiers.GeoEnvoisRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service
public class EnvoisService extends GeoAbstractGraphQLService<GeoEnvois, String> {

  private final EntityManager entityManager;

  public EnvoisService(
      GeoEnvoisRepository paysRepository,
      EntityManager entityManager) {
    super(paysRepository, GeoEnvois.class);
    this.entityManager = entityManager;
  }

  /**
   * > It takes a list of GeoEnvois, finds the original GeoEnvois in the database,
   * merges the two, and
   * returns a list of the merged GeoEnvois.
   * Provided `id` is mandatory and used to fetch the original entity.
   * 
   * @param envois The list of GeoEnvois chunks to be merged.
   * @return A list of GeoEnvois
   */
  public List<GeoEnvois> duplicateMergeAll(List<GeoEnvois> envois) {
    return envois.stream()
        .map(envoi -> {
          GeoEnvois original = this.repository.findById(envoi.getId()).orElseThrow();
          envoi = GeoAbstractGraphQLService.merge(envoi, original, null);
          this.entityManager.detach(envoi);
          envoi.setId(null);
          return envoi;
        })
        .collect(Collectors.toList());
  }

}
