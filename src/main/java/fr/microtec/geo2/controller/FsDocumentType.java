package fr.microtec.geo2.controller;

import fr.microtec.geo2.persistance.StringEnum;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import jdk.jfr.MemoryAddress;
import lombok.Getter;

import java.util.Arrays;

/**
 * File System command can be executed by file manager controller.
 */
@Getter
public enum FsDocumentType implements StringEnum {
	DOCUMENT("document", Maddog2FileSystemService.PATH_KEY.GEO_DOC),
	ETIQUETTE("etiquette", Maddog2FileSystemService.PATH_KEY.GEO_IMG),
    FACTURE("facture", Maddog2FileSystemService.PATH_KEY.GEO_FACTURE),
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
