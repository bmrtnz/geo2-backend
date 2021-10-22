package fr.microtec.geo2.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.common.GeoModification;
import fr.microtec.geo2.persistance.entity.common.GeoModificationCorps;
import fr.microtec.geo2.persistance.repository.common.GeoModifCorpsRepository;
import fr.microtec.geo2.persistance.repository.common.GeoModifRepository;
import fr.microtec.geo2.service.graphql.GeoAbstractGraphQLService;

@Service
public class ModificationService {

	private final GeoModifRepository modificationRepository;
	private final GeoModifCorpsRepository modificationCorpsRepository;

	public ModificationService(
			GeoModifRepository modificationRepository,
			GeoModifCorpsRepository matierePremiereRepository
	) {
		this.modificationRepository = modificationRepository;
		this.modificationCorpsRepository = matierePremiereRepository;
	}

	public GeoModification save(GeoModification chunk) {

    List<GeoModificationCorps> allCorps = chunk.getCorps();
    GeoModification merged = new GeoModification();

    // push actual data
    if(chunk.getId() != null) {
      GeoModification original = this.modificationRepository
      .findById(chunk.getId()).get();
      GeoAbstractGraphQLService.merge(original, merged, null);
    }

    GeoAbstractGraphQLService.merge(chunk, merged, null);

    merged = this.modificationRepository.save(merged);

    if (allCorps != null && !allCorps.isEmpty())
      for (GeoModificationCorps corps : allCorps) {
        GeoModificationCorps mergedCorps = new GeoModificationCorps();
        if(corps.getId() != null) {
          GeoModificationCorps originalCorps = this
          .modificationCorpsRepository
          .findById(corps.getId()).get();
          GeoAbstractGraphQLService.merge(originalCorps, mergedCorps, null);
        }
        GeoAbstractGraphQLService.merge(corps, mergedCorps, null);
        if (mergedCorps.getModification() == null)
          mergedCorps.setModification(merged);
        corps = this.modificationCorpsRepository.save(mergedCorps);
      }
      
    return merged;
	}

}
