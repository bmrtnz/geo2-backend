package fr.microtec.geo2.persistance.entity.document;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;

public interface GeoAsFacture extends GeoBaseDocument {

    void setDocumentFacture(GeoDocument facture);
    GeoDocument getDocumentFacture();

    String getDocumentFactureName();

    String getDocumentFactureOldName();

    default Maddog2FileSystemService.PATH_KEY getDocumentFacturePathKey() {
        return Maddog2FileSystemService.PATH_KEY.GEO_FACTURE;
    }

    default Path getDocumentFactureWithMaddogService(Maddog2FileSystemService fileSystemService) {
        try {
            return fileSystemService.getDocument(this.getDocumentFacturePathKey(), this.getDocumentFactureName(), false);
        } catch (FileSystemNotFoundException ex) {
            // Recherche avec l'ancien format
            return fileSystemService.getDocument(this.getDocumentFacturePathKey(), this.getDocumentFactureOldName(), false);
        }
    }
}
