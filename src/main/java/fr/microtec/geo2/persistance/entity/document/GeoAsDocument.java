package fr.microtec.geo2.persistance.entity.document;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

import java.nio.file.Path;

/**
 * Interface à rajouter sur les entités qui peuvent avoir une étiquette de rattacher.
 */
public interface GeoAsDocument extends GeoBaseDocument {

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
