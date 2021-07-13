package fr.microtec.geo2.persistance.entity.etiquette;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.PostLoad;
import java.nio.file.Path;

/**
 * Auditeur permettant de charger l'objet GeoDocument sur les entités écoutés.
 *
 * Définie sur l'entité écouté si elle possède un fichier document associé.
 */
@Configurable
public class DocumentAuditingListener {

	private ObjectFactory<Maddog2FileSystemService> fileSystemServiceFactory;

	@Autowired
	public void setFileSystemService(ObjectFactory<Maddog2FileSystemService> factory) {
		this.fileSystemServiceFactory = factory;
	}

	@PostLoad
	public void afterLoad(Object entity) {
		if (entity instanceof GeoAsDocument) {
			GeoAsDocument entityWithEtiquette = (GeoAsDocument) entity;
			GeoDocument document = new GeoDocument();

			Maddog2FileSystemService fileSystemService = this.fileSystemServiceFactory.getObject();
			try {
				Path doc = entityWithEtiquette.getDocumentWithMaddogService(fileSystemService);

				document.setIsPresent(true);
				document.setFilename(doc.getFileName().toString());
			} catch (Exception e) {
				document.setIsPresent(false);
			}

			entityWithEtiquette.setDocument(document);
		}
	}
}
