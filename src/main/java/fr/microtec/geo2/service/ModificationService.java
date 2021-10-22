package fr.microtec.geo2.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	public GeoModification save(GeoModification modifChunk) {

    GeoModification modif = new GeoModification();

    if(modifChunk.getId() != null)
      modif = this
      .modificationRepository
      .getOne(modifChunk.getId());

    modif = GeoAbstractGraphQLService.merge(modif, modifChunk, null);
    modif = this.modificationRepository.save(modif);

    for (GeoModificationCorps corps : modif.getCorps()) {
      if (corps.getModification() == null) corps.setModification(modif);
      corps = this.modificationCorpsRepository.save(corps);
    }

		return modif;
	}

}
