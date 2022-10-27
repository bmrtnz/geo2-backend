package fr.microtec.geo2.persistance.entity.document;

import java.nio.file.Path;

import fr.microtec.geo2.service.fs.Maddog2FileSystemService;

public interface GeoAsCQPhoto extends GeoBaseDocument {

    void setCqPhoto(GeoDocument cqPhoto);

    GeoDocument getCqPhoto();

    String getCqPhotoPath();

    default Maddog2FileSystemService.PATH_KEY getCqPhotoPathKey() {
        return Maddog2FileSystemService.PATH_KEY.GEO_CQ_PHOTOS;
    }

    default Path getCqPhotoWithMaddogService(Maddog2FileSystemService fileSystemService) {
        return fileSystemService.getDocument(this.getCqPhotoPathKey(), this.getCqPhotoPath(), false);
    }

}
