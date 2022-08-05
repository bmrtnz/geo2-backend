package fr.microtec.geo2.persistance.entity.document;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

import java.nio.file.Path;

public interface GeoAsCRM extends GeoBaseDocument {

    void setDocumentCRM(GeoDocument facture);
    GeoDocument getDocumentCRM();
    String getDocumentCRMName();

    default Maddog2FileSystemService.PATH_KEY getDocumentCRMPathKey() {
        return Maddog2FileSystemService.PATH_KEY.GEO_RETOUR_PALOX;
    }

    default Path getDocumentCRMWithMaddogService(Maddog2FileSystemService fileSystemService) {
        return fileSystemService.getDocument(this.getDocumentCRMPathKey(), this.getDocumentCRMName(), false);
    }

}
