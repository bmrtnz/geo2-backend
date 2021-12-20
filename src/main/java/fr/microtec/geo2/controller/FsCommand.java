package fr.microtec.geo2.controller;

import fr.microtec.geo2.persistance.StringEnum;
import lombok.Getter;

/**
 * File System command can be executed by file manager controller.
 */
@Getter
public enum FsCommand implements StringEnum {
	LIST("list"), RENAME("rename"), CREATE_DIR("createDir"), COPY("copy"),
	DELETE("delete"), MOVE("move"), UPLOAD("upload"), ABORT_UPLOAD("abort"),
	DOWNLOAD("download");

	private final String key;
	FsCommand(String key) { this.key = key; }

}
