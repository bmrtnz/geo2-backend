package fr.microtec.geo2.persistance.entity.document;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;

public interface GeoAsFacture extends GeoAsDocument {

    // On renomme les methods document en facture.
    default void setDocument(GeoDocument document) {
        this.setDocumentFacture(document);
    }
    void setDocumentFacture(GeoDocument facture);

    default GeoDocument getDocument() {
        return this.getDocumentFacture();
    }
    GeoDocument getDocumentFacture();

    default String getDocumentName() {
        return this.getDocumentFactureName();
    }
    String getDocumentFactureName();

    String getDocumentFactureOldName();

    @Override
    default Maddog2FileSystemService.PATH_KEY getDocumentPathKey() {
        return Maddog2FileSystemService.PATH_KEY.GEO_FACTURE;
    }

    @Override
    default Path getDocumentWithMaddogService(Maddog2FileSystemService fileSystemService) {
        try {
            return fileSystemService.getDocument(this.getDocumentPathKey(), this.getDocumentFactureName(), false);
        } catch (FileSystemNotFoundException ex) {
            // Recherche avec l'ancien format
            return fileSystemService.getDocument(this.getDocumentPathKey(), this.getDocumentFactureOldName(), false);
        }
    }
}
