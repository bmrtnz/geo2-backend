package fr.microtec.geo2.persistance.entity.document;

import java.nio.file.Path;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

public interface GeoAsCQDoc extends GeoBaseDocument {

    void setCqDoc(GeoDocument cqPhoto);

    GeoDocument getCqDoc();

    String getCqDocPath();

    Maddog2FileSystemService.PATH_KEY getCqDocPathKey();

    default Path getCqDocWithMaddogService(Maddog2FileSystemService fileSystemService) {
        return fileSystemService.getDocument(this.getCqDocPathKey(), this.getCqDocPath(), false);
    }

}
