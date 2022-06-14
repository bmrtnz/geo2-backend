package fr.microtec.geo2.persistance.repository.event.document;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

import java.nio.file.Path;

public interface GeoAsDocument {

	// Getter / Setter pour ajouter l'objet etiquette sur les entités.
	void setDocument(GeoDocument document);
	GeoDocument getDocument();

	String getDocumentName();

	default Maddog2FileSystemService.PATH_KEY getDocumentPathKey() {
		return Maddog2FileSystemService.PATH_KEY.GEO_DOC;
	}

	default Path getDocumentWithMaddogService(Maddog2FileSystemService fileSystemService) {
		return fileSystemService.getDocument(this.getDocumentPathKey(), this.getDocumentName(), false);
	}

}
