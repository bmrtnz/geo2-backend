package fr.microtec.geo2.persistance.entity.etiquette;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.PostLoad;
import java.nio.file.Path;

/**
 * Auditeur permettant de charger l'objet GeoEtiquette sur les entités écoutés.
 *
 * Définie sur l'entité écouté si elle possède un fichier étiquette associé.
 */
@Configurable
public class EtiquetteAuditingListener {

	private ObjectFactory<Maddog2FileSystemService> fileSystemServiceFactory;

	@Autowired
	public void setFileSystemService(ObjectFactory<Maddog2FileSystemService> factory) {
		this.fileSystemServiceFactory = factory;
	}

	@PostLoad
	public void afterLoad(Object entity) {
		if (entity instanceof GeoAsEtiquette) {
			GeoAsEtiquette entityWithEtiquette = (GeoAsEtiquette) entity;
			GeoEtiquette etiquette = new GeoEtiquette();

			Maddog2FileSystemService fileSystemService = this.fileSystemServiceFactory.getObject();
			try {
				Path doc = fileSystemService.getEtiquette(entityWithEtiquette.getEtiquetteName(), false);

				etiquette.setIsPresent(true);
				etiquette.setFilename(doc.getFileName().toString());
			} catch (Exception e) {
				etiquette.setIsPresent(false);
			}

			entityWithEtiquette.setEtiquette(etiquette);
		}
	}
}
