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


	public ModificationService(
			GeoModifRepository modificationRepository,
			GeoModifCorpsRepository matierePremiereRepository
	) {}

	public GeoModification prepare(GeoModification modification) {
        if(modification.getCorps() != null) {
			List<GeoModificationCorps> mappedCorps = modification.getCorps()
			.stream()
			.map(corps -> {
				corps.setModification(modification);
				return corps;
			})
			.collect(Collectors.toList());
			modification.setCorps(mappedCorps);
		}
        return modification;
	}

}
