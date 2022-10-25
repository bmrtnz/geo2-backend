package fr.microtec.geo2.persistance.entity.document;

import java.nio.file.Path;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

public interface GeoAsCQTechnique extends GeoBaseDocument {

    void setCqTechnique(GeoDocument facture);

    GeoDocument getCqTechnique();

    String getCqTechniqueName();

    default Maddog2FileSystemService.PATH_KEY getCqTechniquePathKey() {
        return Maddog2FileSystemService.PATH_KEY.GEO_CQ;
    }

    default Path getCqTechniqueWithMaddogService(Maddog2FileSystemService fileSystemService) {
        return fileSystemService.getDocument(this.getCqTechniquePathKey(), this.getCqTechniqueName(), false);
    }

}
