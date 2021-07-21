package fr.microtec.geo2.persistance.entity.etiquette;

import fr.microtec.geo2.controller.FsDocumentType;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.PostLoad;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

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
			GeoAsDocument entityWithDocument = (GeoAsDocument) entity;
			GeoDocument document = new GeoDocument();
			boolean isEtiquette = entityWithDocument instanceof GeoAsEtiquette;

			Maddog2FileSystemService fileSystemService = this.fileSystemServiceFactory.getObject();
			try {
				// File
				Path doc = entityWithDocument.getDocumentWithMaddogService(fileSystemService);
				String filename = isEtiquette ? doc.getFileName().toString() : doc.toString();

				// Uri
				UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
				UriComponents uriComponents = uriBuilder
						.path("/file-manager").path("/")
						.path(isEtiquette ? FsDocumentType.ETIQUETTE.getKey() : FsDocumentType.DOCUMENT.getKey()).path("/")
						.path(Base64.getEncoder().encodeToString(filename.getBytes())).build();

				// Set document type (image as img else iframe)
				String mimeType = Files.probeContentType(doc);
				boolean isImg = (mimeType != null && "image".equals(mimeType.split("/")[0]));

				document.setIsPresent(true);
				document.setUri(uriComponents.toUriString());
				document.setType(isImg ? "img" : "iframe");
			} catch (Exception e) {
				document.setIsPresent(false);
				System.out.println(entityWithDocument.getDocumentName() + " Not found");
			}

			entityWithDocument.setDocument(document);
		}
	}
}
