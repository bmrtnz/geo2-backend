package fr.microtec.geo2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import fr.microtec.geo2.persistance.entity.common.GeoModification;
import fr.microtec.geo2.persistance.entity.common.GeoModificationCorps;
import fr.microtec.geo2.persistance.repository.common.GeoModifCorpsRepository;
import fr.microtec.geo2.persistance.repository.common.GeoModifRepository;

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

    GeoModification modif = this.modificationRepository.save(modifChunk);

    List<GeoModificationCorps> persistedCorps = modif
    .getCorps().stream()
    .map(corps -> {
      if (corps.getModification() == null) corps.setModification(modif);
      return this.modificationCorpsRepository.save(corps);
    })
    .collect(Collectors.toList());

    modif.setCorps(persistedCorps);

		return modif;
	}

}
