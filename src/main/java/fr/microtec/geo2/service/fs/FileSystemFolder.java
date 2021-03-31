package fr.microtec.geo2.service.fs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileSystemFolder implements FileSystemItem {

	private String name;
	private Boolean isDirectory;
	private Boolean hasSubDirectory;

}
