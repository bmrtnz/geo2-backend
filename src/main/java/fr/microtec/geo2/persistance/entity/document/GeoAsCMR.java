package fr.microtec.geo2.persistance.entity.document;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

import java.nio.file.Path;

public interface GeoAsCMR extends GeoBaseDocument {

    void setDocumentCMR(GeoDocument facture);
    GeoDocument getDocumentCMR();
    String getDocumentCMRName();

    default Maddog2FileSystemService.PATH_KEY getDocumentCMRPathKey() {
        return Maddog2FileSystemService.PATH_KEY.GEO_RETOUR_PALOX;
    }

    default Path getDocumentCRMWithMaddogService(Maddog2FileSystemService fileSystemService) {
        return fileSystemService.getDocument(this.getDocumentCMRPathKey(), this.getDocumentCMRName(), false);
    }

}
