package fr.microtec.geo2.service.fs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

/**
 * This service is used for access to maddog2 NAS file system.
 */
@Service
@Secured("ROLE_USER")
public class Maddog2FileSystemService extends FileSystemService {

	/**
	 * PATH_KEY use by client, it's use to order files.
	 */
	public enum PATH_KEY {
		CLIENTS("/Geo2_doc/tiers/clients/"),
		FOURNISSEURS("/Geo2_doc/tiers/fournisseurs/"),
		TRANSPORTEURS("/Geo2_doc/tiers/transporteurs/"),
		LIEUX_PASSAGE_A_QUAI("/Geo2_doc/tiers/lieuxpassageaquai/"),
		ARTICLES("/Geo2_doc/articles/"),
		ORDRES("/Geo2_doc/ordres/"),
		GEO_IMG("/geo_img/");

		private String path;
		PATH_KEY(String basePath) { this.path = basePath; }

		public static PATH_KEY from(String s) {
			for (PATH_KEY pathKey : PATH_KEY.values()) {
				if (pathKey.name().equalsIgnoreCase(s)) return pathKey;
			}

			throw new IllegalArgumentException();
		}
	}

	public Maddog2FileSystemService(@Value("${geo2.maddog2.path}") String basePath) {
		super();
		this.basePath = basePath;
	}

	/**
	 * Build base relative path for given path_key and id.
	 */
	public String buildKeyIdPath(String key, String id) {
		return PATH_KEY.from(key).path + id + "/";
	}

	/**
	 * Get Path of label file from given name, with extension or not.
	 */
	public Path getEtiquette(String filename, boolean withExtension) {
		// Filename do not contains extensions because can be pdf or jpg
		String globPattern = "glob:**/" + filename + (withExtension ? "" : ".{pdf,jpg}");
		List<Path> files = this.list(PATH_KEY.GEO_IMG.path, FileSystems.getDefault().getPathMatcher(globPattern));
		Path downloadFile;

		// If as multiple file, pdf is priority
		if (files.size() > 1) {
			boolean asPdf = files.stream().anyMatch(f -> f.toString().endsWith(".pdf"));

			if (asPdf) {
				downloadFile = files.stream().filter(f -> f.toString().endsWith(".pdf")).findFirst().get();
			} else {
				downloadFile = files.get(0);
			}
		} else if (files.size() == 1) {
			downloadFile = files.get(0);
		} else {
			throw new FileSystemNotFoundException();
		}

		return downloadFile;
	}

}
