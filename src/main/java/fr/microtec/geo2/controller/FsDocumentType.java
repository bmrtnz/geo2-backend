package fr.microtec.geo2.controller;

import fr.microtec.geo2.persistance.StringEnum;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import jdk.jfr.MemoryAddress;
import lombok.Getter;

/**
 * File System command can be executed by file manager controller.
 */
@Getter
public enum FsDocumentType implements StringEnum {
	DOCUMENT("document", Maddog2FileSystemService.PATH_KEY.GEO_DOC),
	ETIQUETTE("etiquette", Maddog2FileSystemService.PATH_KEY.GEO_IMG),
    IMAGE("image", Maddog2FileSystemService.PATH_KEY.GEO_IMG);

	private final String key;
	private final Maddog2FileSystemService.PATH_KEY path;

	FsDocumentType(String key, Maddog2FileSystemService.PATH_KEY path) {
		this.key = key;
		this.path = path;
	}
}
