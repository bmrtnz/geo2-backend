package fr.microtec.geo2.controller;

import java.util.Arrays;
import fr.microtec.geo2.persistance.StringEnum;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import lombok.Getter;

/**
 * File System command can be executed by file manager controller.
 */
@Getter
public enum FsDocumentType implements StringEnum {
    DOCUMENT("document", Maddog2FileSystemService.PATH_KEY.GEO_DOC),
    ETIQUETTE("etiquette", Maddog2FileSystemService.PATH_KEY.GEO_IMG),
    FACTURE("facture", Maddog2FileSystemService.PATH_KEY.GEO_FACTURE),
    CQ("cq", Maddog2FileSystemService.PATH_KEY.GEO_CQ),
    CQDOC("cqdoc", Maddog2FileSystemService.PATH_KEY.GEO_CQ_PDF),
    CQPHOTOS("cqphotos", Maddog2FileSystemService.PATH_KEY.GEO_CQ_PHOTOS),
    CMR("cmr", Maddog2FileSystemService.PATH_KEY.GEO_RETOUR_PALOX),
    IMAGE("image", Maddog2FileSystemService.PATH_KEY.GEO_IMG);

    private final String key;
    private final Maddog2FileSystemService.PATH_KEY path;

    FsDocumentType(String key, Maddog2FileSystemService.PATH_KEY path) {
        this.key = key;
        this.path = path;
    }

    public static FsDocumentType fromPathKey(Maddog2FileSystemService.PATH_KEY pathKey) {
        return Arrays.stream(FsDocumentType.values())
                .filter(fsDocumentType -> fsDocumentType.path.equals(pathKey))
                .findFirst()
                .orElse(null);
    }
}
