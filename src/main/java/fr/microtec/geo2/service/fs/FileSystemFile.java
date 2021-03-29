package fr.microtec.geo2.service.fs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class FileSystemFile implements FileSystemItem {

	private String name;
	private Boolean isDirectory;
	private Long size;
	private Instant dateModified;

}
